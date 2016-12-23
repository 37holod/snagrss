package ua.com.snag.rssreader.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.DataProvider;
import ua.com.snag.rssreader.controller.SettingsManager;
import ua.com.snag.rssreader.controller.SettingsManagerI;
import ua.com.snag.rssreader.model.Orientation;

/**
 * Created by holod on 20.12.16.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected Handler handler;
    private Core core;
    private Orientation orientation;
    private boolean alive;
    private FragmentManager fragmentManager;
    protected SettingsManagerI settingsManager;

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        fragmentManager = getSupportFragmentManager();
        alive = true;
        core = (Core) getApplicationContext();
        core.addToBaseActivityMap(BaseActivity.this);
        handler = new Handler();
        setOrientation();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        alive = false;
        super.onDestroy();
    }

    private void setOrientation() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if (dpWidth >= 600f && getResources().getConfiguration().orientation == Configuration
                .ORIENTATION_LANDSCAPE) {
            orientation = Orientation.HORISONTAL_600DP;
        } else {
            orientation = Orientation.VERTICAL;
        }
    }

    protected void fragmentsReplacer(final int id, final Fragment fragment) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();
                fragmentTransaction.replace(id, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                fragmentManager.popBackStack();
            }
        });
    }

    protected void fragmentAdd(final int id, final Fragment fragment) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();
                fragmentTransaction.add(id, fragment);
                fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
    }

    protected int fragmentsRemoveFromTop(final int id) {
        fragmentRemove(fragmentFinder(id));
        return fragmentManager.getBackStackEntryCount();
    }


    protected void fragmentRemove(final Fragment fragment) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commitAllowingStateLoss();
                fragmentManager.popBackStack();
            }
        });
    }

    protected Fragment fragmentFinder(final int id) {
        return fragmentManager.findFragmentById(id);
    }

    protected ArrayList<?> getListenerByClass(Class listener) {
        return core.getListenerByClass(listener);
    }

    public void showError(final String text) {
        final AlertDialog.Builder alert = new AlertDialog.Builder
                (BaseActivity.this);
        alert.setTitle(getResources().getString(R.string
                .error));
        alert.setMessage(text);
        alert.setPositiveButton(getResources().getString(R.string
                .close), new
                DialogInterface
                        .OnClickListener() {
                    public void onClick(DialogInterface dialog, int
                            whichButton) {

                    }
                });
        handler.post(new Runnable() {
            @Override
            public void run() {
                alert.show();
            }
        });
    }

    public interface WarningListener {

        void okPressed();

    }

    public void showWarning(final String text, final WarningListener warningListener) {
        final AlertDialog.Builder alert = new AlertDialog.Builder
                (BaseActivity.this);
        alert.setTitle(getResources().getString(R.string
                .warning));
        alert.setMessage(text);
        alert.setPositiveButton(getResources().getString(R.string
                .ok), new
                DialogInterface
                        .OnClickListener() {
                    public void onClick(DialogInterface dialog, int
                            whichButton) {
                        warningListener.okPressed();
                    }
                });
        alert.setNegativeButton(getResources().getString(R.string
                .cancel), new
                DialogInterface
                        .OnClickListener() {
                    public void onClick(DialogInterface dialog, int
                            whichButton) {

                    }
                });
        handler.post(new Runnable() {
            @Override
            public void run() {
                alert.show();
            }
        });
    }


}
