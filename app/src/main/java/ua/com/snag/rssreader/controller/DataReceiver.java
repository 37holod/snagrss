package ua.com.snag.rssreader.controller;

/**
 * Created by holod on 02.01.17.
 */

public interface DataReceiver<T> extends AbstractListener {

    void success(T data);
}
