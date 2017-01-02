package ua.com.snag.rssreader.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.ProcessListener;
import ua.com.snag.rssreader.fragments.AddNewFeedFragment;
import ua.com.snag.rssreader.fragments.BaseFragment;
import ua.com.snag.rssreader.fragments.ChangeSettingsListener;
import ua.com.snag.rssreader.fragments.FragmentManagerI;
import ua.com.snag.rssreader.fragments.NaviDrawer;
import ua.com.snag.rssreader.fragments.NaviDrawerListener;
import ua.com.snag.rssreader.fragments.TabRssFragment;
import ua.com.snag.rssreader.model.ChangedSettings;
import ua.com.snag.rssreader.test.IdlingResourceImpl;

public class MainActivity extends BaseActivity implements FragmentManagerI, NaviDrawerListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        initActions(savedInstanceState);
    }


    private void initActions(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragmentAdd(R.id.activity_main_navi_view_fl, new NaviDrawer(), false);
            fragmentAdd(R.id.content_main_fl, new TabRssFragment(), false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_by_newest) {
            setDesc(true);
            return true;
        }
        if (id == R.id.action_by_oldest) {
            setDesc(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void addToContentFragment(final BaseFragment baseFragment, boolean addToBackStack) {
        fragmentAdd(R.id.content_main_fl, baseFragment, addToBackStack);
        closeDrawer();

    }

    @Override
    public void removeFragment(BaseFragment baseFragment) {
        fragmentRemove(baseFragment);
    }

    @Override
    public void closeDrawer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

    }

    public void setDesc(boolean desc) {
        settingsManager.setFeedOrderDesc(new ProcessListener() {
            @Override
            public void success() {
                ChangedSettings changedSettings = new ChangedSettings();
                changedSettings.setFeedDescChanged(true);
                for (Object listener
                        : getListenerByClass(ChangeSettingsListener.class)) {
                    ((ChangeSettingsListener) listener).settingsChanged(changedSettings);
                }
            }

            @Override
            public void error(Exception e) {
                Core.writeLogError(TAG, e);
                showError(e.getMessage());
            }
        }, desc);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResourceImpl getIdlingResource() {
        return idlingResource;
    }

    @VisibleForTesting
    @NonNull
    public void addNewFeedFragment() {
        addToContentFragment(new AddNewFeedFragment(), true);
    }


}
