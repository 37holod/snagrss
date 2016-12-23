package ua.com.snag.rssreader.controller;

/**
 * Created by holod on 23.12.16.
 */

public interface SettingsManagerI {


    interface FetchBooleanValue extends ManagerI.ManagerOperation {
        void success(boolean value);

    }

    void isFeedOrderDesc(FetchBooleanValue fetchBooleanValue);

    void setFeedOrderDesc(ManagerI.InsertListener insertListener, boolean value);
}
