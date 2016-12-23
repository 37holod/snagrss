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
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.ManagerI;
import ua.com.snag.rssreader.model.ChangedSettings;
import ua.com.snag.rssreader.model.Channel;

/**
 * Created by holod on 22.12.16.
 */

public class TabRssFragment extends ContentFragments implements FeedCountListener,
        ChangeSettingsListener {
    private static final String TAG = TabRssFragment.class.getSimpleName();
    private ArrayList<TabItem> itemsList;
    private ViewPager fragment_tab_rss_vp;
    private CustomFragmentPagerAdapter customFragmentPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_rss,
                null, false);
        initFields(view);
        return view;
    }

    private void initFields(View view) {
        itemsList = new ArrayList<>();
        fragment_tab_rss_vp = (ViewPager) view.findViewById(R.id.fragment_tab_rss_vp);
        customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getChildFragmentManager());
        fragment_tab_rss_vp.setAdapter(customFragmentPagerAdapter);
        refreshAdapter();
    }

    private void refreshAdapter() {
        dataProvider.fetchChannelList(new ManagerI.ChannelListReceiver() {
            @Override
            public void success(List<Channel> channelList) {
                final ArrayList<TabItem> tempItemsList = new ArrayList<TabItem>();
                for (Channel channel : channelList) {
                    TabItem tabItem = new TabItem();
                    tabItem.setChannel(channel);
                    ItemTabRssFragment itemTabRssFragment = new ItemTabRssFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(ItemTabRssFragment.CHANNEL_URL_KEY, channel.getUrl());
                    itemTabRssFragment.setArguments(bundle);
                    tabItem.setItemTabRssFragment(itemTabRssFragment);
                    tempItemsList.add(tabItem);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemsList.clear();
                        itemsList.addAll(tempItemsList);
                        customFragmentPagerAdapter.notifyDataSetChanged();
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
    public void addNewFeed(String url) {
        refreshAdapter();
    }

    @Override
    public void removeFeed(String url) {
        refreshAdapter();
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
    public void settingsChanged(ChangedSettings changedSettings) {
        for (TabItem tabItem : itemsList) {
            ((ChangeSettingsListener) tabItem.getItemTabRssFragment()).settingsChanged
                    (changedSettings);
        }
    }

    class TabItem {
        private ItemTabRssFragment itemTabRssFragment;
        private Channel channel;

        public ItemTabRssFragment getItemTabRssFragment() {
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
