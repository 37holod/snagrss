package ua.com.snag.rssreader.controller.file;

import ua.com.snag.rssreader.controller.AbstractExecution;

/**
 * Created by holod on 24.12.16.
 */

public interface SaveImageListener extends AbstractExecution {
    void saveSuccess(String filePath);
}