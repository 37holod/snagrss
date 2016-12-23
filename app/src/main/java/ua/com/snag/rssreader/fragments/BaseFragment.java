package ua.com.snag.rssreader.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import ua.com.snag.rssreader.activities.BaseActivity;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.DataProvider;
import ua.com.snag.rssreader.controller.SettingsManager;
import ua.com.snag.rssreader.controller.SettingsManagerI;
import ua.com.snag.rssreader.model.Orientation;

/**
 * Created by holod on 20.12.16.
 */

public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    private Core core;
    private boolean alive;
    protected Handler handler;
    protected DataProvider dataProvider;
    protected SettingsManagerI settingsManager;

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        core = (Core) getActivity().getApplicationContext();
        handler = new Handler();
        core.addToBaseFragmentsMap(BaseFragment.this);
        alive = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        alive = false;
        super.onDestroy();

    }

    protected ArrayList<?> getListenerByClass(Class listener) {
        return core.getListenerByClass(listener);
    }

    protected void fragmentsReplacer(final int id, final BaseFragment fragment) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(id, fragment);
                transaction.commitAllowingStateLoss();

            }
        });
    }

    public void showWarning(final String text, final BaseActivity.WarningListener warningListener) {
        ((BaseActivity) getActivity()).showWarning(text, warningListener);
    }

    public void showError(final String text) {
        ((BaseActivity) getActivity()).showError(text);
    }

}
