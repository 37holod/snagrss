package ua.com.snag.rssreader.controller.network;

import java.util.List;

import ua.com.snag.rssreader.controller.DataReceiver;
import ua.com.snag.rssreader.controller.ManagerI;
import ua.com.snag.rssreader.model.Channel;

/**
 * Created by holod on 21.12.16.
 */

public interface NetworkManagerI extends ManagerI {
    void fetchChannel(String channelUrl, DataReceiver<List<Channel>> dataReceiver);

}
