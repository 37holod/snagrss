package ua.com.snag.rssreader.controller;

import android.content.Context;
import android.content.SharedPreferences;

import ua.com.snag.rssreader.activities.MainActivity;

/**
 * Created by holod on 23.12.16.
 */

public class SettingsManager implements SettingsManagerI {
    private static final String TAG = SettingsManager.class.getSimpleName();
    private Context context;
    private static final String FILENAME = "settings";
    private static final String ORDER_DESC = "order_desc";
    private SharedPreferences sPref;

    public SettingsManager(Context context) {
        this.context = context;
        sPref = context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
    }

    @Override
    public void isFeedOrderDesc(final FetchBooleanValue fetchBooleanValue) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (SettingsManager.this) {
                    try {
                        boolean value = sPref.getBoolean(ORDER_DESC, false);
                        fetchBooleanValue.success(value);
                    } catch (Exception e) {
                        fetchBooleanValue.error(e);
                    }
                }
            }
        }).start();
    }

    @Override
    public void setFeedOrderDesc(final ManagerI.InsertListener insertListener, final boolean
            value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (SettingsManager.this) {
                    SharedPreferences.Editor ed = null;
                    try {
                        ed = sPref.edit();
                        ed.putBoolean(ORDER_DESC, value);
                        ed.apply();
                        insertListener.success();
                    } catch (Exception e) {
                        insertListener.error(e);
                    }
                }
            }
        }).start();
    }


}
