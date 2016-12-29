package ua.com.snag.rssreader.controller.database;

import ua.com.snag.rssreader.controller.AbstractExecution;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 29.12.16.
 */

public interface RssItemReceiver extends AbstractExecution {
    void success(RssItem rssItem);
}
