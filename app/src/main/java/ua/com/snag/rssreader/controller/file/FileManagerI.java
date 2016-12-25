package ua.com.snag.rssreader.controller.file;

import android.graphics.Bitmap;

import ua.com.snag.rssreader.controller.ManagerI;

/**
 * Created by holod on 21.12.16.
 */

public interface FileManagerI extends ManagerI {


    void saveImage(String path, Bitmap bitmap, SaveImageListener saveImageListener);


}
