package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;

/**
 * Created by holod on 21.12.16.
 */

public interface FileManagerI extends ManagerI {

    interface SaveImageListener extends ManagerI.ManagerOperation {
        void saveSuccess(String filePath);
    }

    void saveImage(String path, Bitmap bitmap, SaveImageListener saveImageListener);


}
