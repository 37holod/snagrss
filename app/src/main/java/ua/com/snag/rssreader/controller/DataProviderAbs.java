package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;

import java.util.List;

import ua.com.snag.rssreader.controller.database.DbManagerI;
import ua.com.snag.rssreader.controller.file.FileManagerI;
import ua.com.snag.rssreader.controller.network.NetworkManagerI;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 21.12.16.
 */

public abstract class DataProviderAbs implements DbManagerI, FileManagerI,
        NetworkManagerI {
    @Override
    public void fetchChannelList(DataReceiver<List<Channel>> dataReceiver) {

    }

    @Override
    public void insertChannelList(List<Channel> channelList, ProcessListener processListener) {

    }

    @Override
    public void insertRssItemList(List<RssItem> channelList, ProcessListener processListener) {

    }

    @Override
    public void removeChannel(String channelUrl, ProcessListener processListener) {

    }


    @Override
    public void loadImage(String path, DataReceiver<Bitmap> dataReceiver, int maxWidth) {

    }

    @Override
    public void fetchChannel(String channelUrl, DataReceiver<List<Channel>> dataReceiver) {

    }


    @Override
    public void fetchRssItemList(String channelUrl, DataReceiver<List<RssItem>>
            dataReceiver,
                                 boolean orderDesc) {

    }

    @Override
    public void saveImage(String path, Bitmap bitmap, DataReceiver<String> dataReceiverr) {

    }

    public void refreshRssItemList(String channelUrl, DataReceiver<List<RssItem>>
            dataReceiver,
                                   boolean orderDesc) {

    }

    @Override
    public void fetchRssItem(String channelUrl, DataReceiver<RssItem> dataReceiver, String link) {

    }
}