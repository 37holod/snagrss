package ua.com.snag.rssreader.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.activities.BaseActivity;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.DataReceiver;
import ua.com.snag.rssreader.controller.ProcessListener;
import ua.com.snag.rssreader.controller.database.DbManagerI;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 21.12.16.
 */

public class NaviDrawer extends BaseFragment implements FeedCountListener {
    private static final String TAG = NaviDrawer.class.getSimpleName();
    private RecyclerView fragment_navi_drawer_rv;
    private ArrayList<ListViewItem> listViewItems;
    private RecyclerAdapter taskListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_navi_drawer,
                null, false);
        initFields(view);
        return view;
    }

    private void initFields(View view) {
        fragment_navi_drawer_rv = (RecyclerView) view.findViewById(R.id.fragment_navi_drawer_rv);
        listViewItems = new ArrayList<>();
        taskListAdapter = new RecyclerAdapter();
        fragment_navi_drawer_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragment_navi_drawer_rv.setItemAnimator(new DefaultItemAnimator());
        fragment_navi_drawer_rv.setAdapter(taskListAdapter);
        refreshData();

    }

    private void refreshData() {
        dataProvider.fetchChannelList(new DataReceiver<List<Channel>>() {
            @Override
            public void success(List<Channel> channelList) {
                refreshListView(channelList);
                if (channelList.isEmpty()) {

                    for (Object listener
                            : getListenerByClass(FragmentManagerI.class)) {
                        ((FragmentManagerI) listener).addToContentFragment
                                (new AddNewFeedFragment(), true);
                    }
                }
            }

            @Override
            public void error(Exception e) {
                Core.writeLogError(TAG, e);
            }
        });
    }

    private void refreshListView(List<Channel> channelList) {
        final ArrayList<ListViewItem> tempListViewItems = new ArrayList<>();
        for (Channel channel : channelList) {
            RssFeed rssFeed = new RssFeed();
            rssFeed.setName(channel.getTitle());
            rssFeed.setChannel(channel);
            tempListViewItems.add(rssFeed);
        }
        AddRssFeed addRssFeed = new AddRssFeed();
        addRssFeed.setName(getResources().getString(R.string.add_rss_feed));
        tempListViewItems.add(addRssFeed);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listViewItems.clear();
                listViewItems.addAll(tempListViewItems);
                taskListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void addNewFeed(Channel channel) {
        refreshData();
    }

    @Override
    public void removeFeed(Channel channel) {
        refreshData();
    }

    @Override
    public void setCurrentFeed(String url) {

    }

    private abstract class ListViewItem {
        private String name;
        private String iconText;

        protected String plus, minus;

        public ListViewItem() {
            plus = getResources().getString(R.string.plus);
            minus = getResources().getString(R.string.minus);
        }

        public abstract String getIconText();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        abstract void onClick();

        abstract void onIconClick();
    }

    private class RssFeed extends ListViewItem {
        private Channel channel;

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        @Override
        public String getIconText() {
            return minus;
        }

        @Override
        void onClick() {
            for (Object listener
                    : getListenerByClass(NaviDrawerListener.class)) {
                ((NaviDrawerListener) listener).closeDrawer();
            }
            for (Object listener
                    : getListenerByClass(FeedCountListener.class)) {
                ((FeedCountListener) listener).setCurrentFeed(channel.getUrl());
            }
        }

        @Override
        void onIconClick() {
            showWarning(getResources().getString(R.string.really_remove_feed), new BaseActivity
                    .WarningListener() {

                @Override
                public void okPressed() {
                    dataProvider.removeChannel(channel.getUrl(), new ProcessListener() {
                        @Override
                        public void success() {
                            for (Object listener
                                    : getListenerByClass(FeedCountListener.class)) {
                                ((FeedCountListener) listener).removeFeed(channel);
                            }
                        }

                        @Override
                        public void error(Exception e) {
                            showError(e.getMessage());
                            Core.writeLogError(TAG, e);
                        }
                    });

                }
            });

        }
    }

    private class AddRssFeed extends ListViewItem {

        @Override
        public String getIconText() {
            return plus;
        }

        @Override
        void onClick() {
            for (Object listener
                    : getListenerByClass(FragmentManagerI.class)) {
                ((FragmentManagerI) listener).addToContentFragment(new
                        AddNewFeedFragment(), true);
            }
        }

        @Override
        void onIconClick() {
            onClick();
        }
    }


    private class RecyclerAdapter extends RecyclerView.Adapter<NaviDrawer.RecyclerAdapter
            .CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView navi_adapter_lv_item_name_tv, navi_adapter_lv_item_icon_tv;
            RelativeLayout navi_adapter_lv_item_name_rl, navi_adapter_lv_item_icon_rl;

            CustomViewHolder(View view) {
                super(view);
                navi_adapter_lv_item_name_tv = (TextView) view.findViewById(R.id
                        .navi_adapter_lv_item_name_tv);
                navi_adapter_lv_item_icon_tv = (TextView) view.findViewById(R.id
                        .navi_adapter_lv_item_icon_tv);
                navi_adapter_lv_item_name_rl = (RelativeLayout) view
                        .findViewById(R.id
                                .navi_adapter_lv_item_name_rl);
                navi_adapter_lv_item_icon_rl = (RelativeLayout) view
                        .findViewById(R.id
                                .navi_adapter_lv_item_icon_rl);
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.navi_adapter_lv_item, parent, false);

            return new CustomViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(final CustomViewHolder holder, int position) {
            final ListViewItem listViewItem = listViewItems.get(position);
            holder.navi_adapter_lv_item_name_tv.setText(listViewItem.getName());
            holder.navi_adapter_lv_item_icon_tv.setText(listViewItem.getIconText());
            holder.navi_adapter_lv_item_name_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listViewItem.onClick();
                }
            });
            holder.navi_adapter_lv_item_icon_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listViewItem.onIconClick();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listViewItems.size();
        }
    }
}
