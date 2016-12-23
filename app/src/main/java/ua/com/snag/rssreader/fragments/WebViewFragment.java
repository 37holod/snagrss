package ua.com.snag.rssreader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ua.com.snag.rssreader.R;
import ua.com.snag.rssreader.controller.Core;

/**
 * Created by holod on 22.12.16.
 */

public class WebViewFragment extends BaseFragment {
    public static final String RSS_ITEM_URL_KEY = "RSS_ITEM_URL_KEY";
    private static final String TAG = WebViewFragment.class.getSimpleName();
    private String url;
    private WebView fragment_web_view_vw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view,
                null, false);
        initFields(view);
        return view;
    }

    private void initFields(View view) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        url = bundle.getString(RSS_ITEM_URL_KEY);
        fragment_web_view_vw = (WebView) view.findViewById(R.id.fragment_web_view_vw);
        fragment_web_view_vw.setWebViewClient(new WebViewClient());
        fragment_web_view_vw.setWebChromeClient(new WebChromeClient());
        fragment_web_view_vw.getSettings().setBuiltInZoomControls(true);
        fragment_web_view_vw.getSettings().setDisplayZoomControls(false);
        fragment_web_view_vw.getSettings().setLoadWithOverviewMode(true);
        fragment_web_view_vw.getSettings().setUseWideViewPort(true);
        fragment_web_view_vw.loadUrl(url);
    }
}
