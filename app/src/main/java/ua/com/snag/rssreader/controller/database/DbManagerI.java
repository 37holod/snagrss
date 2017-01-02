package ua.com.snag.rssreader.controller.database;

import java.util.List;

import ua.com.snag.rssreader.controller.DataReceiver;
import ua.com.snag.rssreader.controller.ManagerI;
import ua.com.snag.rssreader.controller.ProcessListener;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;


/**
 * Created by holod on 21.12.16.
 */

public interface DbManagerI extends ManagerI {


    void fetchChannelList(DataReceiver<List<Channel>> dataReceiver);


    void insertChannelList(List<Channel> channelList, ProcessListener processListener);

    void insertRssItemList(List<RssItem> channelList, ProcessListener processListener);


    void removeChannel(String channelUrl, ProcessListener processListener);

    void fetchRssItem(final String channelUrl, final DataReceiver<RssItem> dataReceiver, final
    String link);


}
