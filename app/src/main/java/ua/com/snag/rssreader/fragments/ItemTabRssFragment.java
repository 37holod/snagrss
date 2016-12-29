package ua.com.snag.rssreader.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.LoadImageListener;
import ua.com.snag.rssreader.controller.RssItemListReceiver;
import ua.com.snag.rssreader.controller.database.RssItemReceiver;
import ua.com.snag.rssreader.controller.settings.FetchBooleanValue;
import ua.com.snag.rssreader.model.ChangedSettings;
import ua.com.snag.rssreader.model.RssItem;
import ua.com.snag.rssreader.utils.RssConst;

/**
 * Created by holod on 22.12.16.
 */

public class ItemTabRssFragment extends PagerPage {
    private static final String TAG = ItemTabRssFragment.class.getSimpleName();
    public static final String CHANNEL_URL_KEY = "CHANNEL_URL_KEY";
    public static final int MAX_SYMBOLS = 400;
    private ArrayList<RssItem> rssItemList;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView fragment_item_tab_rss_rcv;
    private String channelUrl;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean orderDesc;
    private int maxImageWidth;

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
        maxImageWidth = (int) getResources().getDimension(R.dimen.recycle_item_image_size);
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
        settingsManager.isFeedOrderDesc(new FetchBooleanValue() {
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
                dataProvider.refreshRssItemList(channelUrl, new RssItemListReceiver() {
                    @Override
                    public void success(List<RssItem> rssItemList) {
                        notifyAdapter(rssItemList);
                        setSwipeRefreshing(false);
                    }

                    @Override
                    public void error(Exception e) {
                        Core.writeLogError(TAG, e);
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
        dataProvider.fetchRssItemList(channelUrl, new RssItemListReceiver() {
            @Override
            public void success(final List<RssItem> tempRssItemList) {
                notifyAdapter(tempRssItemList);
            }

            @Override
            public void error(Exception e) {
                showError(e.getMessage());
                Core.writeLogError(TAG, e);
            }
        }, orderDesc);

    }

    @Override
    public void settingsChanged(ChangedSettings changedSettings) {

        if (changedSettings.isFeedDescChanged()) {
            settingsManager.isFeedOrderDesc(new FetchBooleanValue() {
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
        SimpleDateFormat simpleDateFormat;

        RecyclerAdapter() {
            readMore = Html.fromHtml(getResources().getString(R.string.read_more));
            simpleDateFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView tab_rss_recycler_item_title_tv, tab_rss_recycler_item_descr_tv,
                    tab_rss_recycler_item_more_tv, tab_rss_recycler_item_date;
            ImageView tab_rss_recycler_item_title_iv;

            CustomViewHolder(View view) {
                super(view);
                tab_rss_recycler_item_title_tv = (TextView) view.findViewById(R.id
                        .tab_rss_recycler_item_title_tv);
                tab_rss_recycler_item_descr_tv = (TextView) view.findViewById(R.id
                        .tab_rss_recycler_item_descr_tv);
                tab_rss_recycler_item_more_tv = (TextView) view.findViewById(R.id
                        .tab_rss_recycler_item_more_tv);
                tab_rss_recycler_item_date = (TextView) view.findViewById(R.id
                        .tab_rss_recycler_item_date);
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
            holder.tab_rss_recycler_item_more_tv.setText(readMore);
            View.OnClickListener onClickListener = createClickListener(rssItem);
            holder.tab_rss_recycler_item_more_tv.setOnClickListener(onClickListener);
            try {
                holder.tab_rss_recycler_item_date.setText(simpleDateFormat.format(new
                        Date(Long.parseLong(rssItem.getPubDate()))));
            } catch (IllegalArgumentException e) {
                holder.tab_rss_recycler_item_date.setText(rssItem.getPubDate());
                Core.writeLogError(TAG, e);
            }
            bindPicture(holder, onClickListener, rssItem);
            bindDescription(holder, onClickListener, rssItem);

        }

        private View.OnClickListener createClickListener(final RssItem rssItem) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchBrowser(rssItem.getLink());
                }
            };
        }

        private void launchBrowser(String link) {
            for (Object listener
                    : getListenerByClass(FragmentManagerI.class)) {
                WebViewFragment webViewFragment = new WebViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString(WebViewFragment.RSS_ITEM_URL_KEY, link);
                webViewFragment.setArguments(bundle);
                ((FragmentManagerI) listener).addToContentFragment(webViewFragment, true);
            }
        }

        private void bindDescription(CustomViewHolder holder, View.OnClickListener
                onClickListener, RssItem rssItem) {
            final TextView descriptionTv = holder.tab_rss_recycler_item_descr_tv;
            descriptionTv.setText(null);
            dataProvider.fetchRssItem(rssItem.getChannel(), new RssItemReceiver() {
                void setDescription(final String description) {
                    setTextViewHTML(descriptionTv, description);

                }

                void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan
                        span) {
                    int start = strBuilder.getSpanStart(span);
                    int end = strBuilder.getSpanEnd(span);
                    int flags = strBuilder.getSpanFlags(span);
                    ClickableSpan clickable = new ClickableSpan() {
                        public void onClick(View view) {
                            launchBrowser(span.getURL());
                        }
                    };
                    strBuilder.setSpan(clickable, start, end, flags);
                    strBuilder.removeSpan(span);
                }

                void setTextViewHTML(final TextView text, String html) {
                    html = html.replaceAll(RssConst.IMAGE_REG, "");
                    CharSequence sequence = Html.fromHtml(html);
                    final SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
                    URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
                    for (URLSpan span : urls) {
                        makeLinkClickable(strBuilder, span);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(strBuilder);
                            text.setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    });

                }


                @Override
                public void success(final RssItem fullDescrRssItem) {
                    setDescription(fullDescrRssItem
                            .getShortDescription());

                }

                @Override
                public void error(Exception e) {
                    Core.writeLogError(TAG, e);
                    setDescription(e.getMessage());
                }
            }, rssItem.getLink());
        }

        private void bindPicture(CustomViewHolder holder, View.OnClickListener onClickListener,
                                 RssItem rssItem) {
            final ImageView tab_rss_recycler_item_title_iv = holder.tab_rss_recycler_item_title_iv;
            tab_rss_recycler_item_title_iv.setOnClickListener(onClickListener);
            if (rssItem.getImageUrl() != null) {
                tab_rss_recycler_item_title_iv.setImageBitmap(null);
                dataProvider.loadImage(rssItem.getImageUrl(), new LoadImageListener() {
                    @Override
                    public void loadSuccess(final Bitmap bitmap) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tab_rss_recycler_item_title_iv.setImageBitmap(bitmap);
                            }
                        });
                    }

                    @Override
                    public void error(Exception e) {
                        Core.writeLogError(TAG, e);
                    }
                }, maxImageWidth);
            } else {
                tab_rss_recycler_item_title_iv.setImageResource(R.mipmap.ic_launcher);
            }
        }


        @Override
        public int getItemCount() {
            return rssItemList.size();
        }
    }
}
