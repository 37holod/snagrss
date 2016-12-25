package ua.com.snag.rssreader.controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import ua.com.snag.rssreader.controller.ChannelListReceiver;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.LoadImageListener;
import ua.com.snag.rssreader.controller.RssItemListReceiver;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;

/**
 * Created by holod on 21.12.16.
 */

public class DbManager extends SQLiteOpenHelper implements DbManagerI {
    public static final String DB_NAME = "Ressreaderdb";
    public static final String INTEGER_TYPE = "integer";
    public static final String TEXT_TYPE = "text";
    private static final String TAG = DbManager.class.getSimpleName();
    public static int DB_VERSION = 1;
    private ThreadPoolExecutor executor;

    public DbManager(Context context, ThreadPoolExecutor executor) {
        super(context, DB_NAME, null, DB_VERSION);
        this.executor = executor;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createRssItemTable(db);
        createChannelTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void createRssItemTable(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + RssItem.TABLE_NAME + " (");
        sb.append(RssItem.ID + " " + INTEGER_TYPE + " primary key " +
                "autoincrement,");
        sb.append(RssItem.CHANNEL + " " + TEXT_TYPE + ",");
        sb.append(RssItem.LINK + " " + TEXT_TYPE + ",");
        sb.append(RssItem.PUB_DATE + " " + TEXT_TYPE + ",");
        sb.append(RssItem.TITLE + " " + TEXT_TYPE + ",");
        sb.append(RssItem.IMAGE_URL + " " + TEXT_TYPE + ",");
        sb.append(RssItem.IMAGE_PATH + " " + TEXT_TYPE + ",");
        sb.append(RssItem.SHORT_DESCRIPTION + " " + TEXT_TYPE + ", unique(" + RssItem.CHANNEL +
                "," +
                RssItem.LINK + "));");
        db.execSQL(sb.toString());
    }

    private void createChannelTable(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + Channel.TABLE_NAME + " (");
        sb.append(Channel.ID + " " + INTEGER_TYPE + " primary key " +
                "autoincrement,");
        sb.append(Channel.URL + " " + TEXT_TYPE + " unique,");
        sb.append(Channel.TITLE + " " + TEXT_TYPE + ",");
        sb.append(Channel.LINK + " " + TEXT_TYPE + ",");
        sb.append(Channel.CHANNEL_DESCRIPTION + " " + TEXT_TYPE + ");");
        db.execSQL(sb.toString());
    }


    @Override
    public void fetchChannelList(final ChannelListReceiver channelListFetching) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (DbManager.this) {
                    Cursor cursor = null;
                    try {
                        cursor = getReadableDatabase().rawQuery(
                                "select * from " + Channel.TABLE_NAME, null);
                        ArrayList<Channel> channelList = fillChannelList(cursor);
                        channelListFetching.success(channelList);
                    } catch (Exception e) {
                        channelListFetching.error(e);
                    } finally {
                        try {
                            cursor.close();
                            getReadableDatabase().close();
                        } catch (Exception e) {
                            Core.writeLogError(TAG, e);
                        }
                    }
                }
            }
        });
    }

    private ArrayList<Channel> fillChannelList(Cursor cursor)
            throws Exception {
        ArrayList<Channel> channelList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Channel channel = new Channel();
                channel.setId(cursor.getLong(cursor.getColumnIndex
                        (Channel.ID)));
                channel.setUrl(cursor.getString(cursor.getColumnIndex
                        (Channel.URL)));
                channel.setTitle(cursor.getString(cursor.getColumnIndex
                        (Channel.TITLE)));
                channel.setLink(cursor.getString(cursor.getColumnIndex
                        (Channel.LINK)));

                channel.setChannelDescription(cursor.getString(cursor.getColumnIndex
                        (Channel.CHANNEL_DESCRIPTION)));
                channelList.add(channel);
            } while (cursor.moveToNext());
        }
        return channelList;
    }


    @Override
    public void fetchRssItemList(final String channelUrl, final RssItemListReceiver
            rssItemListReceiver, final boolean orderDesc) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (DbManager.this) {
                    Cursor cursor = null;
                    try {
                        String desc = orderDesc ? " DESC" : "";
                        cursor = getReadableDatabase().rawQuery(
                                "select * from " + RssItem.TABLE_NAME + " where " +
                                        RssItem.CHANNEL + " = ? order by " + RssItem
                                        .PUB_DATE + desc, new
                                        String[]{channelUrl});
                        ArrayList<RssItem> rssItemList = fillRssItemList(cursor);
                        rssItemListReceiver.success(rssItemList);
                    } catch (Exception e) {
                        rssItemListReceiver.error(e);
                    } finally {
                        try {
                            cursor.close();
                            getReadableDatabase().close();
                        } catch (Exception e) {
                            Core.writeLogError(TAG, e);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void loadImage(String path, LoadImageListener loadImageListener) {

    }

    private ArrayList<RssItem> fillRssItemList(Cursor cursor) {
        ArrayList<RssItem> rssItemList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                RssItem rssItem = new RssItem();
                rssItem.setId(cursor.getLong(cursor.getColumnIndex
                        (RssItem.ID)));
                rssItem.setChannel(cursor.getString(cursor.getColumnIndex
                        (RssItem.CHANNEL)));
                rssItem.setLink(cursor.getString(cursor.getColumnIndex
                        (RssItem.LINK)));
                rssItem.setPubDate(cursor.getString(cursor.getColumnIndex
                        (RssItem.PUB_DATE)));
                rssItem.setTitle(cursor.getString(cursor.getColumnIndex
                        (RssItem.TITLE)));
                rssItem.setImageUrl(cursor.getString(cursor.getColumnIndex
                        (RssItem.IMAGE_URL)));
                rssItem.setImagePath(cursor.getString(cursor.getColumnIndex
                        (RssItem.IMAGE_PATH)));

                rssItem.setShortDescription(cursor.getString(cursor.getColumnIndex
                        (RssItem.SHORT_DESCRIPTION)));
                rssItemList.add(rssItem);
            } while (cursor.moveToNext());
        }
        return rssItemList;
    }

    @Override
    public void insertChannelList(final List<Channel> channelList, final ManagerInsertListener
            dbInsertListener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (DbManager.this) {
                    try {
                        insertingChannelList(channelList,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        dbInsertListener.insertingSuccess();
                    } catch (Exception e) {
                        dbInsertListener.error(e);
                    } finally {
                        try {
                            getWritableDatabase().close();
                        } catch (Exception e) {
                            Core.writeLogError(TAG, e);
                        }
                    }
                }
            }
        });
    }

    private void insertingChannelList(List<Channel> channelList, int
            conflict) throws Exception {
        for (Channel channel : channelList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Channel.URL, channel.getUrl());
            contentValues.put(Channel.TITLE, channel.getTitle());
            contentValues.put(Channel.LINK, channel.getLink());
            contentValues.put(Channel.CHANNEL_DESCRIPTION, channel.getChannelDescription());
            getWritableDatabase().insertWithOnConflict(Channel.TABLE_NAME, null,
                    contentValues, conflict);
        }
    }

    @Override
    public void insertRssItemList(final List<RssItem> rssItemList, final ManagerInsertListener
            dbInsertListener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (DbManager.this) {
                    try {
                        insertingRssItemList(rssItemList,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        dbInsertListener.insertingSuccess();
                    } catch (Exception e) {
                        dbInsertListener.error(e);
                    } finally {
                        try {
                            getWritableDatabase().close();
                        } catch (Exception e) {
                            Core.writeLogError(TAG, e);
                        }
                    }
                }
            }
        });
    }

    private void insertingRssItemList(List<RssItem> rssItemList, int
            conflict) throws Exception {
        for (RssItem rssItem : rssItemList) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(RssItem.CHANNEL, rssItem.getChannel());
            contentValues.put(RssItem.LINK, rssItem.getLink());
            contentValues.put(RssItem.TITLE, rssItem.getTitle());
            contentValues.put(RssItem.SHORT_DESCRIPTION, rssItem.getShortDescription());
            contentValues.put(RssItem.PUB_DATE, rssItem.getPubDate());
            contentValues.put(RssItem.IMAGE_URL, rssItem.getImageUrl());
            contentValues.put(RssItem.IMAGE_PATH, rssItem.getImagePath());
            getWritableDatabase().insertWithOnConflict(RssItem.TABLE_NAME, null,
                    contentValues, conflict);
        }
    }

    public void removeChannel(final String channelUrl, final ManagerRemoveListener
            managerRemoveListener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (DbManager.this) {
                    try {
                        getWritableDatabase().delete(Channel.TABLE_NAME, Channel
                                .URL + " = ?", new String[]{channelUrl});
                        managerRemoveListener.removingSuccess();
                    } catch (Exception e) {
                        managerRemoveListener.error(e);
                    } finally {
                        try {
                            getWritableDatabase().close();
                        } catch (Exception e) {
                            Core.writeLogError(TAG, e);
                        }
                    }
                }
            }
        });

    }

}
