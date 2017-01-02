package ua.com.snag.rssreader.controller.settings;


import ua.com.snag.rssreader.controller.DataReceiver;
import ua.com.snag.rssreader.controller.ProcessListener;

/**
 * Created by holod on 23.12.16.
 */

public interface SettingsManagerI {

    void isFeedOrderDesc(DataReceiver<Boolean> dataReceiver);

    void setFeedOrderDesc(ProcessListener processListener, boolean value);
}
