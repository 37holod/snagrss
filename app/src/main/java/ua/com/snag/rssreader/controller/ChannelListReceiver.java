package ua.com.snag.rssreader.controller;

import java.util.List;

import ua.com.snag.rssreader.model.Channel;

/**
 * Created by holod on 24.12.16.
 */

public interface ChannelListReceiver extends AbstractExecution {
    void success(List<Channel> channelList);
}