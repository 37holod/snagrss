package ua.com.snag.rssreader.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.ChannelListReceiver;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.model.ChangedSettings;
import ua.com.snag.rssreader.model.Channel;

/**
 * Created by holod on 22.12.16.
 */

public class TabRssFragment extends ContentFragments implements FeedCountListener,
        ChangeSettingsListener {
    private static final String TAG = TabRssFragment.class.getSimpleName();
    private static final String SAVE_PAGE_NUMBER = "SAVE_PAGE_NUMBER";
    private ArrayList<TabItem> itemsList;
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
        dataProvider.fetchChannelList(new ChannelListReceiver() {
            @Override
            public void success(List<Channel> channelList) {
                final ArrayList<TabItem> tempItemsList = new ArrayList<TabItem>();
                for (Channel channel : channelList) {
                    TabItem tabItem = createTabItem(channel);
                    tempItemsList.add(tabItem);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemsList.clear();
                        itemsList.addAll(tempItemsList);
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

    private TabItem createTabItem(Channel channel) {
        TabItem tabItem = new TabItem();
        tabItem.setChannel(channel);
        ItemTabRssFragment itemTabRssFragment = new ItemTabRssFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ItemTabRssFragment.CHANNEL_URL_KEY, channel.getUrl());
        itemTabRssFragment.setArguments(bundle);
        tabItem.setItemTabRssFragment(itemTabRssFragment);
        return tabItem;
    }

    @Override
    public void addNewFeed(final Channel channel) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                TabItem tabItem = createTabItem(channel);
                itemsList.add(tabItem);
                customFragmentPagerAdapter.notifyDataSetChanged();
                fragment_tab_rss_vp.setCurrentItem(itemsList.indexOf(tabItem));
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
                    TabItem tabItem = searchTab(url);
                    fragment_tab_rss_vp.setCurrentItem(itemsList.indexOf(tabItem), true);
                } catch (Exception e) {
                    Core.writeLogError(TAG, e);
                }

            }
        });
    }

    private TabItem searchTab(String url) throws Exception {
        for (TabItem tabItem : itemsList) {
            if (url.equals(tabItem.getChannel().getUrl())) {
                return tabItem;
            }
        }
        throw new Exception("no tabs");
    }

    @Override
    public void settingsChanged(final ChangedSettings changedSettings) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < customFragmentPagerAdapter.getCount(); i++) {
                    ((PagerPage) customFragmentPagerAdapter.getItem(i)).settingsChanged
                            (changedSettings);
                }
            }
        });
    }

    class TabItem {
        private PagerPage itemTabRssFragment;
        private Channel channel;

        public PagerPage getItemTabRssFragment() {
            return itemTabRssFragment;
        }

        public void setItemTabRssFragment(ItemTabRssFragment itemTabRssFragment) {
            this.itemTabRssFragment = itemTabRssFragment;
        }

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }
    }

    private class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BaseFragment getItem(int position) {
            return itemsList.get(position).getItemTabRssFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return itemsList.get(position).getChannel().getTitle();
        }

        @Override
        public int getCount() {
            return itemsList.size();
        }

    }


}
