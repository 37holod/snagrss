package ua.com.snag.rssreader.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.webkit.URLUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by holod on 21.12.16.
 */

public class FileManager implements FileManagerI {

    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    @Override
    public void saveImage(final String path, final Bitmap bitmap, final SaveImageListener
            saveImageListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (FileManager.this) {
                    FileOutputStream out = null;
                    try {
                        String fileName = URLUtil.guessFileName(path, null, null);
                        File file = new File(context.getExternalFilesDir(Environment
                                .DIRECTORY_PICTURES), fileName);
                        out = new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        saveImageListener.saveSuccess(file.getPath());
                    } catch (Exception e) {
                        saveImageListener.error(e);
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            saveImageListener.error(e);
                        }
                    }
                }
            }
        }).start();

    }

    @Override
    public void fetchRssItemList(String channelUrl, RssItemListReceiver rssItemListReceiver,
                                 boolean orderDesc) {

    }

    @Override
    public void loadImage(final String path, final LoadImageListener loadImageListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (FileManager.this) {
                    try {
                        String fileName = URLUtil.guessFileName(path, null, null);
                        File file = new File(context.getExternalFilesDir(Environment
                                .DIRECTORY_PICTURES), fileName);
                        if (!file.exists()) {
                            loadImageListener.error(new FileNotFoundException());
                            return;
                        }
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                        loadImageListener.loadSuccess(bitmap);
                    } catch (Exception e) {
                        loadImageListener.error(e);
                    }
                }
            }
        }).start();
    }
}
