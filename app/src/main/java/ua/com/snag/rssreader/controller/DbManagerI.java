package ua.com.snag.rssreader.controller;

import java.util.List;

import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;


/**
 * Created by holod on 21.12.16.
 */

public interface DbManagerI extends ManagerI {

    interface ManagerInsertListener extends ManagerOperation {
        void insertingSuccess();
    }

    void fetchChannelList(ChannelListReceiver channelListFetching);


    void insertChannelList(List<Channel> channelList, ManagerInsertListener
            dbInsertListener);

    void insertRssItemList(List<RssItem> channelList, ManagerInsertListener
            dbInsertListener);

    interface ManagerRemoveListener extends ManagerOperation {
        void removingSuccess();
    }

    void removeChannel(String channelUrl, ManagerRemoveListener managerRemoveListener);


}
