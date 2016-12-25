package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;

import java.util.List;

import ua.com.snag.rssreader.controller.database.DbManagerI;
import ua.com.snag.rssreader.controller.database.ManagerInsertListener;
import ua.com.snag.rssreader.controller.database.ManagerRemoveListener;
import ua.com.snag.rssreader.controller.file.FileManagerI;
import ua.com.snag.rssreader.controller.file.SaveImageListener;
import ua.com.snag.rssreader.controller.network.NetworkManagerI;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 21.12.16.
 */

public abstract class DataProviderAbs implements DbManagerI, FileManagerI,
        NetworkManagerI {
    @Override
    public void fetchChannelList(ChannelListReceiver channelListFetching) {

    }

    @Override
    public void insertChannelList(List<Channel> channelList, ManagerInsertListener
            dbInsertListener) {

    }

    @Override
    public void insertRssItemList(List<RssItem> channelList, ManagerInsertListener
            dbInsertListener) {

    }

    @Override
    public void removeChannel(String channelUrl, ManagerRemoveListener managerRemoveListener) {

    }


    @Override
    public void loadImage(String path, LoadImageListener loadImageListener) {

    }

    @Override
    public void fetchChannel(String channelUrl, ChannelListReceiver channelListFetching) {

    }


    @Override
    public void fetchRssItemList(String channelUrl, RssItemListReceiver rssItemListReceiver,
                                 boolean orderDesc) {

    }

    @Override
    public void saveImage(String path, Bitmap bitmap, SaveImageListener saveImageListener) {

    }

    public void refreshRssItemList(String channelUrl, RssItemListReceiver rssItemListReceiver,
                                   boolean orderDesc) {

    }
}
