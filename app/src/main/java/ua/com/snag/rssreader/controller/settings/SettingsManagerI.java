package ua.com.snag.rssreader.controller.settings;

import ua.com.snag.rssreader.controller.AsyncExecutionListener;

/**
 * Created by holod on 23.12.16.
 */

public interface SettingsManagerI {

    void isFeedOrderDesc(FetchBooleanValue fetchBooleanValue);

    void setFeedOrderDesc(AsyncExecutionListener asyncExecutionListener, boolean value);
}
