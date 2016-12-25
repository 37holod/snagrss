package ua.com.snag.rssreader.fragments;

import ua.com.snag.rssreader.model.Channel;

/**
 * Created by holod on 22.12.16.
 */

public interface FeedCountListener {
    void addNewFeed(Channel channel);

    void removeFeed(Channel channel);

    void setCurrentFeed(String url);
}
