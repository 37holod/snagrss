package ua.com.snag.rssreader.controller;

import android.app.Application;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ua.com.snag.rssreader.activities.BaseActivity;
import ua.com.snag.rssreader.controller.database.DbManager;
import ua.com.snag.rssreader.controller.file.FileManager;
import ua.com.snag.rssreader.controller.network.NetworkManager;
import ua.com.snag.rssreader.controller.settings.SettingsManager;
import ua.com.snag.rssreader.fragments.BaseFragment;
import ua.com.snag.rssreader.test.IdlingResourceImpl;

/**
 * Created by holod on 20.12.16.
 */

public class Core extends Application {
    private static final String TAG = Core.class.getSimpleName();
    private HashMap<String, SoftReference> baseActivityHashMap;
    private HashMap<String, SoftReference> baseFragmentHashMap;
    private BaseActivity currentActivity;
    private DataProvider dataProvider;
    private SettingsManager settingsManager;
    private ThreadPoolExecutor executor;
    private IdlingResourceImpl idlingResource;

    @Override
    public void onCreate() {
        baseActivityHashMap = new HashMap<>();
        baseFragmentHashMap = new HashMap<>();
        int numderOfCores = Runtime.getRuntime().availableProcessors();

        executor = new ThreadPoolExecutor(
                numderOfCores * 4,
                numderOfCores * 4,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        settingsManager = new SettingsManager(getApplicationContext(), executor);
        dataProvider = new DataProvider();
        dataProvider.setSettingsManager(settingsManager);
        dataProvider.setDbManager(new DbManager(getApplicationContext(), executor));
        dataProvider.setFileManager(new FileManager(getApplicationContext(), executor));
        dataProvider.setNetworkManager(new NetworkManager(executor));
        idlingResource = new IdlingResourceImpl();
        super.onCreate();
    }

    public static void writeLog(String tag, String text) {
        Log.d(tag, text + " <" + Thread.currentThread().getName() + ">");
    }

    public static void writeLogError(String tag, Exception e) {
        Log.e(tag, e.getMessage(), e);
    }

    public BaseActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(BaseActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void addToBaseActivityMap(BaseActivity baseActivity) {
        currentActivity = baseActivity;
        baseActivityHashMap.put(baseActivity.getClass().toString(), new
                SoftReference<BaseActivity>(baseActivity));
        baseActivity.setSettingsManager(settingsManager);
        baseActivity.setIdlingResource(idlingResource);
    }

    public void addToBaseFragmentsMap(BaseFragment baseFragment) {
        baseFragmentHashMap.put(baseFragment.getClass().toString(), new
                SoftReference<BaseFragment>(baseFragment));
        baseFragment.setDataProvider(dataProvider);
        baseFragment.setSettingsManager(settingsManager);
        baseFragment.setIdlingResource(idlingResource);
    }


    private ArrayList<?> getList(Class clazz) {
        ArrayList<Object> arrayList = new ArrayList<>();
        iter(arrayList, baseFragmentHashMap.entrySet().iterator(), clazz);
        iter(arrayList, baseActivityHashMap.entrySet().iterator(), clazz);
        return arrayList;
    }

    private void iter(ArrayList<Object> arrayList, Iterator<?> it, Class clazz) {
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            Object obj = ((SoftReference) pair.getValue()).get();
            if (obj == null) {
                continue;
            }
            if (clazz.isInstance(((SoftReference) pair.getValue()).get())) {
                arrayList.add(((SoftReference) pair.getValue()).get());
            }
        }
    }

    public ArrayList<?> getListenerByClass(Class listener) {
        return getList(listener);
    }
}
