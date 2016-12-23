package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;

import java.util.List;

import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 21.12.16.
 */

public interface ManagerI {
    interface ManagerOperation {
        void error(Exception e);
    }

    interface InsertListener extends ManagerI.ManagerOperation {
        void success();
    }

    interface ChannelListReceiver extends ManagerOperation {
        void success(List<Channel> channelList);
    }

    interface RssItemListReceiver extends ManagerOperation {
        void success(List<RssItem> rssItemList);
    }

    void fetchRssItemList(String channelUrl, RssItemListReceiver rssItemListReceiver, boolean
            orderDesc);

    interface LoadImageListener extends ManagerI.ManagerOperation {
        void loadSuccess(Bitmap bitmap);
    }

    void loadImage(String path, LoadImageListener loadImageListener);
}
