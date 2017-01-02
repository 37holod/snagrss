package ua.com.snag.rssreader.controller.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.ThreadPoolExecutor;

import ua.com.snag.rssreader.controller.DataReceiver;
import ua.com.snag.rssreader.controller.ProcessListener;


/**
 * Created by holod on 23.12.16.
 */

public class SettingsManager implements SettingsManagerI {
    private static final String TAG = SettingsManager.class.getSimpleName();
    private Context context;
    private static final String FILENAME = "settings";
    private static final String ORDER_DESC = "order_desc";
    private SharedPreferences sPref;
    private ThreadPoolExecutor executor;

    public SettingsManager(Context context, ThreadPoolExecutor executor) {
        this.context = context;
        this.executor = executor;
        sPref = context.getSharedPreferences(FILENAME,
                Context.MODE_PRIVATE);
    }

    @Override
    public void isFeedOrderDesc(final DataReceiver<Boolean> dataReceiver) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (SettingsManager.this) {

                    try {
                        boolean value = sPref.getBoolean(ORDER_DESC, false);
                        dataReceiver.success(value);
                    } catch (Exception e) {
                        dataReceiver.error(e);
                    }

                }
            }
        });
    }

    @Override
    public void setFeedOrderDesc(final ProcessListener processListener, final boolean
            value) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (SettingsManager.this) {
                    SharedPreferences.Editor ed = null;
                    try {
                        ed = sPref.edit();
                        ed.putBoolean(ORDER_DESC, value);
                        ed.apply();
                        processListener.success();
                    } catch (Exception e) {
                        processListener.error(e);
                    }


                }
            }
        });
    }


}
