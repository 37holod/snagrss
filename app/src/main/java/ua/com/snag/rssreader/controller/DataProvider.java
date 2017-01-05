package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;

import java.util.List;

import ua.com.snag.rssreader.controller.database.DbManagerI;
import ua.com.snag.rssreader.controller.file.FileManagerI;
import ua.com.snag.rssreader.controller.network.NetworkManagerI;
import ua.com.snag.rssreader.controller.settings.SettingsManagerI;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 21.12.16.
 */

public class DataProvider extends DataProviderAbs {
    private static final String TAG = DataProvider.class.getSimpleName();
    private DbManagerI dbManager;
    private FileManagerI fileManager;
    private NetworkManagerI networkManager;
    private SettingsManagerI settingsManager;


    public void setSettingsManager(SettingsManagerI settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setDbManager(DbManagerI dbManager) {
        this.dbManager = dbManager;
    }

    public void setFileManager(FileManagerI fileManager) {
        this.fileManager = fileManager;
    }

    public void setNetworkManager(NetworkManagerI networkManager) {
        this.networkManager = networkManager;
    }


    @Override
    public void fetchChannelList(DataReceiver<List<Channel>> dataReceiver) {
        dbManager.fetchChannelList(dataReceiver);
    }

    @Override
    public void fetchChannel(String channelUrl, final DataReceiver<List<Channel>> dataReceiver) {
        networkManager.fetchChannel(channelUrl, new DataReceiver<List<Channel>>() {
            @Override
            public void success(final List<Channel> channelList) {

                dbManager.insertChannelList(channelList, new ProcessListener() {
                    @Override
                    public void success() {
                        dataReceiver.success(channelList);
                    }

                    @Override
                    public void error(Exception e) {
                        dataReceiver.error(e);
                        Core.writeLogError(TAG, e);
                    }
                });
            }

            @Override
            public void error(Exception e) {
                dataReceiver.error(e);
                Core.writeLogError(TAG, e);
            }
        });
    }

    @Override
    public void refreshRssItemList(final String channelUrl, final DataReceiver<List<RssItem>>
            dataReceiver, final boolean orderDesc) {
        networkManager.fetchRssItemList(channelUrl, new DataReceiver<List<RssItem>>() {
            @Override
            public void success(final List<RssItem> rssItemList) {

                dbManager.insertRssItemList(rssItemList, new ProcessListener() {
                    @Override
                    public void success() {
                        dbManager.fetchRssItemList(channelUrl, new DataReceiver<List<RssItem>>() {
                            @Override
                            public void success(List<RssItem> rssItemList) {
                                dataReceiver.success(rssItemList);
                            }

                            @Override
                            public void error(Exception e) {
                                dataReceiver.error(e);
                            }
                        }, orderDesc);
                    }

                    @Override
                    public void error(Exception e) {
                        dataReceiver.error(e);
                    }
                });
            }

            @Override
            public void error(Exception e) {
                dataReceiver.error(e);
            }
        }, orderDesc);
    }

    @Override
    public void fetchRssItemList(final String channelUrl, final DataReceiver<List<RssItem>>
            dataReceiver, final boolean orderDesc) {
        dbManager.fetchRssItemList(channelUrl, new DataReceiver<List<RssItem>>() {
            @Override
            public void success(List<RssItem> rssItemList) {
                if (rssItemList.isEmpty()) {
                    refreshRssItemList(channelUrl, dataReceiver, orderDesc);
                } else {
                    dataReceiver.success(rssItemList);
                }
            }

            @Override
            public void error(Exception e) {
                Core.writeLogError(TAG, e);
            }
        }, orderDesc);
    }


    @Override
    public void loadImage(final String path, final DataReceiver<Bitmap> dataReceiver, final int
            maxWidth) {
        fileManager.loadImage(path, new DataReceiver<Bitmap>() {
            @Override
            public void success(Bitmap bitmap) {
                if (bitmap != null) {
                    dataReceiver.success(bitmap);
                } else {
                    networkManager.loadImage(path, new DataReceiver<Bitmap>() {
                        @Override
                        public void success(Bitmap bitmap) {
                            dataReceiver.success(bitmap);
                            fileManager.saveImage(path, bitmap, new DataReceiver<String>() {
                                @Override
                                public void success(String filePath) {

                                }

                                @Override
                                public void error(Exception e) {
                                    Core.writeLogError(TAG, e);
                                }
                            });
                        }

                        @Override
                        public void error(Exception e) {
                            dataReceiver.error(e);
                            Core.writeLogError(TAG, e);
                        }
                    }, maxWidth);
                }
            }

            @Override
            public void error(Exception e) {
                Core.writeLogError(TAG, e);
            }
        }, maxWidth);
    }

    @Override
    public void removeChannel(String channelUrl, ProcessListener processListener) {
        dbManager.removeChannel(channelUrl, processListener);
    }

    @Override
    public void fetchRssItem(String channelUrl, DataReceiver<RssItem> dataReceiver, String link) {
        dbManager.fetchRssItem(channelUrl, dataReceiver, link);
    }
}