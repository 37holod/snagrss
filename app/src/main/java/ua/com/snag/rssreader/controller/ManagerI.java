package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;

import java.util.List;

import ua.com.snag.rssreader.controller.database.RssItemReceiver;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 21.12.16.
 */

public interface ManagerI {


    void fetchRssItemList(String channelUrl, RssItemListReceiver rssItemListReceiver, boolean
            orderDesc);

    void loadImage(String path, LoadImageListener loadImageListener, int maxWidth);


}
