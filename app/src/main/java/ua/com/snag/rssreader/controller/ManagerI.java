package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;

import java.util.List;

import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 21.12.16.
 */

public interface ManagerI {


    void fetchRssItemList(String channelUrl, DataReceiver<List<RssItem>> dataReceiver, boolean
            orderDesc);

    void loadImage(String path, DataReceiver<Bitmap> dataReceiver, int maxWidth);


}
