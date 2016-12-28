package ua.com.snag.rssreader.fragments;

import ua.com.snag.rssreader.model.ChangedSettings;

/**
 * Created by holod on 28.12.16.
 */

public abstract class PagerPage extends BaseFragment {

    abstract void settingsChanged(ChangedSettings changedSettings);
}
