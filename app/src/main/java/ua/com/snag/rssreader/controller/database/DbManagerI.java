package ua.com.snag.rssreader.controller.database;

import java.util.List;

import ua.com.snag.rssreader.controller.ChannelListReceiver;
import ua.com.snag.rssreader.controller.ManagerI;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;


/**
 * Created by holod on 21.12.16.
 */

public interface DbManagerI extends ManagerI {


    void fetchChannelList(ChannelListReceiver channelListFetching);


    void insertChannelList(List<Channel> channelList, ManagerInsertListener
            dbInsertListener);

    void insertRssItemList(List<RssItem> channelList, ManagerInsertListener
            dbInsertListener);


    void removeChannel(String channelUrl, ManagerRemoveListener managerRemoveListener);


}
