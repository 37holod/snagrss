package ua.com.snag.rssreader.fragments;

/**
 * Created by holod on 21.12.16.
 */

public interface FragmentManagerI {
    void replaceContentFragment(final BaseFragment baseFragment);

    void addToContentFragment(final BaseFragment baseFragment);

    void removeFragment(final BaseFragment baseFragment);

}
