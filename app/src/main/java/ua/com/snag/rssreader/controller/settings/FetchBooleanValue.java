package ua.com.snag.rssreader.controller.settings;

import ua.com.snag.rssreader.controller.AbstractExecution;

/**
 * Created by holod on 24.12.16.
 */

public interface FetchBooleanValue extends AbstractExecution {
    void success(boolean value);

}
