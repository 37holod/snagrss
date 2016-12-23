package ua.com.snag.rssreader.fragments;

/**
 * Created by holod on 22.12.16.
 */

public interface FeedCountListener {
    void addNewFeed(String url);

    void removeFeed(String url);

    void setCurrentFeed(String url);
}
