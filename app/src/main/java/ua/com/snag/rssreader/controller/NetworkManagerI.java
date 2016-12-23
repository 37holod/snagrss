package ua.com.snag.rssreader.controller;

/**
 * Created by holod on 21.12.16.
 */

public interface NetworkManagerI extends ManagerI {
    void fetchChannel(String channelUrl, ChannelListReceiver channelListFetching);

}
