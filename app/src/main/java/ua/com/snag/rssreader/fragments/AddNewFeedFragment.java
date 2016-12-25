package ua.com.snag.rssreader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.List;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.ChannelListReceiver;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.model.Channel;

/**
 * Created by holod on 22.12.16.
 */

public class AddNewFeedFragment extends ContentFragments {
    private static final String TAG = AddNewFeedFragment.class.getSimpleName();
    private EditText fragment_add_new_feed_url_et;
    private Button fragment_add_new_feed_bt;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_new_feed,
                null, false);
        initFields(view);

        return view;
    }

    private void setPb(final int visibility) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(visibility);
            }
        });

    }

    private void initFields(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_add_new_feed_url_pb);
        setPb(View.INVISIBLE);
        fragment_add_new_feed_url_et = (EditText) view.findViewById(R.id
                .fragment_add_new_feed_url_et);
        fragment_add_new_feed_bt = (Button) view.findViewById(R.id.fragment_add_new_feed_bt);
        fragment_add_new_feed_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                setPb(View.VISIBLE);
                idlingResource.setIdleState(false);
                dataProvider.fetchChannel(fragment_add_new_feed_url_et.getText().toString(), new
                        ChannelListReceiver() {
                            @Override
                            public void success(List<Channel> channelList) {
                                setPb(View.INVISIBLE);
                                idlingResource.setIdleState(true);
                                if (channelList.isEmpty()) {
                                    error(new Exception("empty list"));
                                    return;
                                }
                                for (Object listener
                                        : getListenerByClass(FeedCountListener.class)) {
                                    ((FeedCountListener) listener).addNewFeed
                                            (channelList.get(0));
                                }
                                for (Object listener
                                        : getListenerByClass(FragmentManagerI.class)) {
                                    ((FragmentManagerI) listener).removeFragment
                                            (AddNewFeedFragment.this);
                                }
                            }

                            @Override
                            public void error(Exception e) {
                                idlingResource.setIdleState(true);
                                setPb(View.INVISIBLE);
                                showError(e.getMessage());
                                Core.writeLogError(TAG, e);
                            }
                        });

            }
        });
    }


}
