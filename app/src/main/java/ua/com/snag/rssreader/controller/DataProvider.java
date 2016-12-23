package ua.com.snag.rssreader.controller;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.FileNotFoundException;
import java.util.List;

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

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public DataProvider(Context context) {
        dbManager = new DbManager(context);
        fileManager = new FileManager(context);
        networkManager = new NetworkManager();
    }

    @Override
    public void fetchChannelList(ChannelListReceiver channelListFetching) {
        dbManager.fetchChannelList(channelListFetching);
    }

    @Override
    public void fetchChannel(String channelUrl, final ChannelListReceiver channelListFetching) {
        networkManager.fetchChannel(channelUrl, new ChannelListReceiver() {
            @Override
            public void success(final List<Channel> channelList) {

                dbManager.insertChannelList(channelList, new ManagerInsertListener() {
                    @Override
                    public void insertingSuccess() {
                        channelListFetching.success(channelList);
                    }

                    @Override
                    public void error(Exception e) {
                        channelListFetching.error(e);
                        Core.writeLogError(TAG, e);
                    }
                });
            }

            @Override
            public void error(Exception e) {
                channelListFetching.error(e);
                Core.writeLogError(TAG, e);
            }
        });
    }

    @Override
    public void refreshRssItemList(final String channelUrl, final RssItemListReceiver
            rssItemListReceiver, final boolean orderDesc) {
        networkManager.fetchRssItemList(channelUrl, new RssItemListReceiver() {
            @Override
            public void success(final List<RssItem> rssItemList) {

                dbManager.insertRssItemList(rssItemList, new ManagerInsertListener() {
                    @Override
                    public void insertingSuccess() {
                        dbManager.fetchRssItemList(channelUrl, new RssItemListReceiver() {
                            @Override
                            public void success(List<RssItem> rssItemList) {
                                rssItemListReceiver.success(rssItemList);
                            }

                            @Override
                            public void error(Exception e) {
                                Core.writeLogError(TAG, e);
                            }
                        }, orderDesc);
                    }

                    @Override
                    public void error(Exception e) {
                        Core.writeLogError(TAG, e);
                    }
                });
            }

            @Override
            public void error(Exception e) {
                Core.writeLogError(TAG, e);
            }
        }, orderDesc);
    }

    @Override
    public void fetchRssItemList(final String channelUrl, final RssItemListReceiver
            rssItemListReceiver, final boolean orderDesc) {
        dbManager.fetchRssItemList(channelUrl, new RssItemListReceiver() {
            @Override
            public void success(List<RssItem> rssItemList) {
                if (rssItemList.isEmpty()) {
                    refreshRssItemList(channelUrl, rssItemListReceiver, orderDesc);
                } else {
                    rssItemListReceiver.success(rssItemList);
                }
            }

            @Override
            public void error(Exception e) {
                Core.writeLogError(TAG, e);
            }
        }, orderDesc);
    }


    @Override
    public void loadImage(final String path, final LoadImageListener loadImageListener) {
        fileManager.loadImage(path, new LoadImageListener() {
            @Override
            public void loadSuccess(Bitmap bitmap) {
                loadImageListener.loadSuccess(bitmap);
            }

            @Override
            public void error(Exception e) {
                if (e instanceof FileNotFoundException) {
                    networkManager.loadImage(path, new LoadImageListener() {
                        @Override
                        public void loadSuccess(Bitmap bitmap) {
                            loadImageListener.loadSuccess(bitmap);
                            fileManager.saveImage(path, bitmap, new SaveImageListener() {
                                @Override
                                public void saveSuccess(String filePath) {

                                }

                                @Override
                                public void error(Exception e) {
                                    Core.writeLogError(TAG, e);
                                }
                            });
                        }

                        @Override
                        public void error(Exception e) {
                            Core.writeLogError(TAG, e);
                        }
                    });
                } else {
                    Core.writeLogError(TAG, e);
                }
            }
        });
    }

    @Override
    public void removeChannel(String channelUrl, ManagerRemoveListener managerRemoveListener) {
        dbManager.removeChannel(channelUrl, managerRemoveListener);
    }
}
