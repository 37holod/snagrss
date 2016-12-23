package ua.com.snag.rssreader.controller;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ua.com.snag.rssreader.activities.BaseActivity;
import ua.com.snag.rssreader.fragments.BaseFragment;

/**
 * Created by holod on 20.12.16.
 */

public class Core extends Application {
    private static final String TAG = Core.class.getSimpleName();
    private HashMap<String, BaseActivity> baseActivityHashMap;
    private HashMap<String, BaseFragment> baseFragmentHashMap;
    private BaseActivity currentActivity;
    private DataProvider dataProvider;
    private SettingsManager settingsManager;

    @Override
    public void onCreate() {
        baseActivityHashMap = new HashMap<>();
        baseFragmentHashMap = new HashMap<>();
        dataProvider = new DataProvider(getApplicationContext());
        settingsManager = new SettingsManager(getApplicationContext());
        dataProvider.setSettingsManager(settingsManager);
        super.onCreate();
    }

    public static void writeLog(String tag, String text) {
        Log.d(tag, text + " <" + Thread.currentThread().getName() +
                ">");
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
        writeLog(TAG, "addToBaseActivityMap " + baseActivity.getClass().getSimpleName());
        currentActivity = baseActivity;
        baseActivityHashMap.put(baseActivity.getClass().toString(), baseActivity);
        baseActivity.setSettingsManager(settingsManager);
    }

    public void addToBaseFragmentsMap(BaseFragment baseFragment) {
        writeLog(TAG, "addToBaseActivityMap " + baseFragment.getClass().getSimpleName());
        baseFragmentHashMap.put(baseFragment.getClass().toString(), baseFragment);
        baseFragment.setDataProvider(dataProvider);
        baseFragment.setSettingsManager(settingsManager);
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
            if (clazz.isInstance(pair.getValue())) {
                arrayList.add(pair.getValue());
            }
        }
    }

    public ArrayList<?> getListenerByClass(Class listener) {
        return getList(listener);
    }
}
