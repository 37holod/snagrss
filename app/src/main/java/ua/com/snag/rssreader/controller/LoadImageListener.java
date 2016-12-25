package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;

/**
 * Created by holod on 24.12.16.
 */

public interface LoadImageListener extends AbstractExecution {
    void loadSuccess(Bitmap bitmap);
}
