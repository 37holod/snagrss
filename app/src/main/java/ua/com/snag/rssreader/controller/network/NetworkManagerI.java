package ua.com.snag.rssreader.controller.network;

import ua.com.snag.rssreader.controller.ChannelListReceiver;
import ua.com.snag.rssreader.controller.ManagerI;

/**
 * Created by holod on 21.12.16.
 */

public interface NetworkManagerI extends ManagerI {
    void fetchChannel(String channelUrl, ChannelListReceiver channelListFetching);

}
