package ua.com.snag.rssreader.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.ManagerI;
import ua.com.snag.rssreader.controller.SettingsManagerI;
import ua.com.snag.rssreader.model.ChangedSettings;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 22.12.16.
 */

public class ItemTabRssFragment extends BaseFragment implements ChangeSettingsListener {
    private static final String TAG = ItemTabRssFragment.class.getSimpleName();
    public static final String CHANNEL_URL_KEY = "CHANNEL_URL_KEY";
    private ArrayList<RssItem> rssItemList;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView fragment_item_tab_rss_rcv;
    private String channelUrl;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean orderDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_tab_rss,
                null, false);
        initFields(view);
        return view;
    }


    private void initFields(View view) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        channelUrl = bundle.getString(CHANNEL_URL_KEY);

        fragment_item_tab_rss_rcv = (RecyclerView) view.findViewById(R.id
                .fragment_item_tab_rss_rcv);
        rssItemList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter();
        fragment_item_tab_rss_rcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragment_item_tab_rss_rcv.setItemAnimator(new DefaultItemAnimator());
        fragment_item_tab_rss_rcv.setAdapter(recyclerAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id
                .fragment_item_tab_rss_srl);
        initActions();
        settingsManager.isFeedOrderDesc(new SettingsManagerI.FetchBooleanValue() {
            @Override
            public void success(boolean value) {
                orderDesc = value;
                refreshData();
            }

            @Override
            public void error(Exception e) {
                Core.writeLogError(TAG, e);
            }
        });

    }

    private void initActions() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataProvider.refreshRssItemList(channelUrl, new ManagerI.RssItemListReceiver() {
                    @Override
                    public void success(List<RssItem> rssItemList) {
                        notifyAdapter(rssItemList);
                        setSwipeRefreshing(false);
                    }

                    @Override
                    public void error(Exception e) {
                        showError(e.getMessage());
                        setSwipeRefreshing(false);
                    }
                }, orderDesc);
            }
        });
    }

    private void setSwipeRefreshing(final boolean bool) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(bool);
            }
        });
    }

    private void notifyAdapter(final List<RssItem> tempRssItemList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                rssItemList.clear();
                rssItemList.addAll(tempRssItemList);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void refreshData() {
        dataProvider.fetchRssItemList(channelUrl, new ManagerI.RssItemListReceiver() {
            @Override
            public void success(final List<RssItem> tempRssItemList) {
                notifyAdapter(tempRssItemList);
            }

            @Override
            public void error(Exception e) {
                showError(e.getMessage());
            }
        }, orderDesc);

    }

    @Override
    public void settingsChanged(ChangedSettings changedSettings) {
        if (changedSettings.isFeedDescChanged()) {
            settingsManager.isFeedOrderDesc(new SettingsManagerI.FetchBooleanValue() {
                @Override
                public void success(boolean value) {
                    orderDesc = value;
                    refreshData();
                }

                @Override
                public void error(Exception e) {
                    Core.writeLogError(TAG, e);
                }
            });
        }
    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder> {
        Spanned readMore;

        RecyclerAdapter() {
            readMore = Html.fromHtml(getResources().getString(R.string.read_more));
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView tab_rss_recycler_item_title_tv, tab_rss_recycler_item_descr_tv,
                    tab_rss_recycler_item_more_tv;
            ImageView tab_rss_recycler_item_title_iv;

            CustomViewHolder(View view) {
                super(view);
                tab_rss_recycler_item_title_tv = (TextView) view.findViewById(R.id
                        .tab_rss_recycler_item_title_tv);
                tab_rss_recycler_item_descr_tv = (TextView) view.findViewById(R.id
                        .tab_rss_recycler_item_descr_tv);
                tab_rss_recycler_item_more_tv = (TextView) view.findViewById(R.id
                        .tab_rss_recycler_item_more_tv);
                tab_rss_recycler_item_title_iv = (ImageView) view.findViewById(R.id
                        .tab_rss_recycler_item_title_iv);
            }
        }


        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tab_rss_recycler_item, parent, false);

            return new CustomViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder holder, int position) {
            final RssItem rssItem = rssItemList.get(position);
            holder.tab_rss_recycler_item_title_tv.setText(rssItem.getTitle());
            holder.tab_rss_recycler_item_descr_tv.setText(rssItem.getShortDescription());

            final ImageView iv = holder.tab_rss_recycler_item_title_iv;
            holder.tab_rss_recycler_item_more_tv.setText(readMore);
            holder.tab_rss_recycler_item_more_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (Object listener
                            : getListenerByClass(FragmentManagerI.class)) {
                        WebViewFragment webViewFragment = new WebViewFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(WebViewFragment.RSS_ITEM_URL_KEY, rssItem.getLink());
                        webViewFragment.setArguments(bundle);
                        ((FragmentManagerI) listener).addToContentFragment(webViewFragment);
                    }
                }
            });

            if (rssItem.getImageUrl() != null) {
                iv.setImageBitmap(null);
                dataProvider.loadImage(rssItem.getImageUrl(), new ManagerI.LoadImageListener() {
                    @Override
                    public void loadSuccess(final Bitmap bitmap) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                iv.setImageBitmap(bitmap);
                            }
                        });
                    }

                    @Override
                    public void error(Exception e) {
                        Core.writeLogError(TAG, e);
                    }
                });
            } else {
                iv.setImageResource(R.mipmap.ic_launcher);
            }
        }

        @Override
        public int getItemCount() {
            return rssItemList.size();
        }
    }
}
