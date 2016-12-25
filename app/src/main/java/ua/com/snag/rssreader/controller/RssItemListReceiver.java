package ua.com.snag.rssreader.controller;

import java.util.List;

import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 24.12.16.
 */

public interface RssItemListReceiver extends AbstractExecution {
    void success(List<RssItem> rssItemList);
}
