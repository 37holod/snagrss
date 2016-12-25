package ua.com.snag.rssreader.fragments;

/**
 * Created by holod on 21.12.16.
 */

public interface FragmentManagerI {
    void addToContentFragment(BaseFragment baseFragment, boolean addToBackStack);

    void removeFragment(BaseFragment baseFragment);

}
