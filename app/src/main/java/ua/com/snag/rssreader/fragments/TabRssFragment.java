package ua.com.snag.rssreader.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.DataReceiver;
import ua.com.snag.rssreader.model.ChangedSettings;
import ua.com.snag.rssreader.model.Channel;

/**
 * Created by holod on 22.12.16.
 */

public class TabRssFragment extends ContentFragments implements FeedCountListener,
        ChangeSettingsListener {
    private static final String TAG = TabRssFragment.class.getSimpleName();
    private static final String SAVE_PAGE_NUMBER = "SAVE_PAGE_NUMBER";
    private ArrayList<Channel> itemsList;
    private ViewPager fragment_tab_rss_vp;
    private CustomFragmentPagerAdapter customFragmentPagerAdapter;
    private int currentPage;
    private Bundle savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        View view = inflater.inflate(R.layout.fragment_tab_rss,
                null, false);
        initFields(view);
        return view;
    }

    private void initFields(View view) {
        currentPage = -1;
        itemsList = new ArrayList<>();
        fragment_tab_rss_vp = (ViewPager) view.findViewById(R.id.fragment_tab_rss_vp);
        customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getChildFragmentManager());
        fragment_tab_rss_vp.setAdapter(customFragmentPagerAdapter);
        fragment_tab_rss_vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        refreshAdapter();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, currentPage);
    }


    private void refreshAdapter() {
        dataProvider.fetchChannelList(new DataReceiver<List<Channel>>() {
            @Override
            public void success(final List<Channel> tempChannelList) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemsList.clear();
                        itemsList.addAll(tempChannelList);
                        customFragmentPagerAdapter.notifyDataSetChanged();
                        if (savedInstanceState != null) {
                            currentPage = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
                            try {
                                fragment_tab_rss_vp.setCurrentItem(currentPage, false);
                            } catch (Exception e) {
                                Core.writeLogError(TAG, e);
                            }
                        }

                    }
                });
            }

            @Override
            public void error(Exception e) {
                Core.writeLogError(TAG, e);
            }
        });
    }

    @Override
    public void addNewFeed(final Channel channel) {
        if (itemsList.contains(channel)) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                itemsList.add(channel);
                customFragmentPagerAdapter.notifyDataSetChanged();
                fragment_tab_rss_vp.setCurrentItem(itemsList.indexOf(channel));
            }
        });
    }

    @Override
    public void removeFeed(final Channel channel) {
        for (Object listener
                : getListenerByClass(FragmentManagerI.class)) {
            ((FragmentManagerI) listener).addToContentFragment(new TabRssFragment(), false);
        }
    }

    @Override
    public void setCurrentFeed(final String url) {
        if (url.isEmpty()) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Channel channel = searchTab(url);
                    fragment_tab_rss_vp.setCurrentItem(itemsList.indexOf(channel), true);
                } catch (Exception e) {
                    Core.writeLogError(TAG, e);
                }

            }
        });
    }

    private Channel searchTab(String url) throws Exception {
        for (Channel channel : itemsList) {
            if (url.equals(channel.getUrl())) {
                return channel;
            }
        }
        throw new Exception("no tabs");
    }

    @Override
    public void settingsChanged(final ChangedSettings changedSettings) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Iterator entries = customFragmentPagerAdapter.getAliveFragments();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    ((PagerPage) entry.getValue()).settingsChanged(changedSettings);
                }
            }
        });
    }


    private class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {

        CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            pagerMap = new HashMap<>();
        }

        HashMap<Integer, PagerPage> pagerMap;

        Iterator<Map.Entry<Integer, PagerPage>> getAliveFragments() {
            return pagerMap.entrySet().iterator();
        }


        @Override
        public BaseFragment getItem(int position) {
            Channel channel = itemsList.get(position);
            PagerPage pagerPage = new ItemTabRssFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ItemTabRssFragment.CHANNEL_URL_KEY, channel.getUrl());
            pagerPage.setArguments(bundle);
            pagerMap.put(position, pagerPage);
            return pagerPage;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            pagerMap.remove(position);
            super.destroyItem(container, position, object);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return itemsList.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return itemsList.size();
        }

    }


}
