package ua.com.snag.rssreader.fragments;

import ua.com.snag.rssreader.model.ChangedSettings;

/**
 * Created by holod on 23.12.16.
 */

public interface ChangeSettingsListener {
    void settingsChanged(ChangedSettings changedSettings);
}
