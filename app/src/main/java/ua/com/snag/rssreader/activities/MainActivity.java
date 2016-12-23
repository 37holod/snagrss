package ua.com.snag.rssreader.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.ManagerI;
import ua.com.snag.rssreader.fragments.BaseFragment;
import ua.com.snag.rssreader.fragments.ChangeSettingsListener;
import ua.com.snag.rssreader.fragments.FeedCountListener;
import ua.com.snag.rssreader.fragments.NaviDrawer;
import ua.com.snag.rssreader.fragments.FragmentManagerI;
import ua.com.snag.rssreader.fragments.NaviDrawerListener;
import ua.com.snag.rssreader.fragments.TabRssFragment;
import ua.com.snag.rssreader.model.ChangedSettings;

public class MainActivity extends BaseActivity implements FragmentManagerI, NaviDrawerListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
            fragmentsReplacer(R.id.activity_main_navi_view_fl, new NaviDrawer());
            replaceContentFragment(new TabRssFragment());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentsRemoveFromTop(R.id
                    .content_main_fl) < 2) {
                super.onBackPressed();
            }
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
    public void replaceContentFragment(final BaseFragment baseFragment) {

        fragmentsReplacer(R.id.content_main_fl, baseFragment);
        closeDrawer();

    }

    @Override
    public void addToContentFragment(final BaseFragment baseFragment) {

        fragmentAdd(R.id.content_main_fl, baseFragment);
        closeDrawer();

    }

    @Override
    public void removeFragment(BaseFragment baseFragment) {
        fragmentRemove(baseFragment);
    }

    @Override
    public void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void setDesc(boolean desc) {
        settingsManager.setFeedOrderDesc(new ManagerI.InsertListener() {
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
                showError(e.getMessage());
            }
        }, desc);
    }
}
