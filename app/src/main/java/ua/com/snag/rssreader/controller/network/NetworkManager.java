package ua.com.snag.rssreader.controller.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ua.com.snag.rssreader.controller.ChannelListReceiver;
import ua.com.snag.rssreader.controller.Core;
import ua.com.snag.rssreader.controller.LoadImageListener;
import ua.com.snag.rssreader.controller.RssItemListReceiver;
import ua.com.snag.rssreader.model.Channel;
import ua.com.snag.rssreader.model.RssItem;
import ua.com.snag.rssreader.utils.RssConst;

/**
 * Created by holod on 21.12.16.
 */


public class NetworkManager implements NetworkManagerI {
    private static final String TAG = NetworkManager.class.getSimpleName();
    private static final String TAG_CHANNEL = "channel";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LINK = "link";
    private static final String TAG_DESRIPTION = "description";
    private static final String TAG_ITEM = "item";
    private static final String TAG_PUB_DATE = "pubDate";
    private ThreadPoolExecutor executor;

    public NetworkManager(ThreadPoolExecutor executor) {
        this.executor = executor;
    }


    @Override
    public void fetchRssItemList(final String channelUrl, final RssItemListReceiver
            rssItemListReceiver, boolean orderDesc) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<RssItem> rssItemList = new ArrayList<RssItem>();
                HttpURLConnection httpURLConnection = null;
                InputStream inputStream = null;
                try {
                    httpURLConnection = createConnection(channelUrl);
                    inputStream = httpURLConnection.getInputStream();
                    Document doc = createDocument(inputStream);
                    NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
                    Element e = (Element) nodeList.item(0);
                    NodeList items = e.getElementsByTagName(TAG_ITEM);
                    for (int i = 0; i < items.getLength(); i++) {
                        Node node = items.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element e1 = (Element) items.item(i);
                            RssItem rssItem = new RssItem();
                            rssItem.setLink(getValue(e1, TAG_LINK));
                            rssItem.setTitle(getValue(e1, TAG_TITLE));
                            rssItem.setChannel(channelUrl);
                            String pubDate = getValue(e1, TAG_PUB_DATE);
                            try {
                                pubDate = Long.toString(RssConst.DATE_FORMAT.parse(pubDate)
                                        .getTime());
                            } catch (Exception ex) {
                                Core.writeLogError(TAG, ex);
                            }
                            rssItem.setPubDate(pubDate);
                            String description = getValue(e1, TAG_DESRIPTION);
                            rssItem.setShortDescription(createShortMessageBody(description));
                            rssItem.setImageUrl(fetchPicture(node.getTextContent()));
                            rssItemList.add(rssItem);
                        }
                    }
                    rssItemListReceiver.success(rssItemList);

                } catch (Exception e) {
                    rssItemListReceiver.error(e);
                } finally {
                    closeConnections(inputStream, httpURLConnection);
                }
            }
        });

    }

    private void closeConnections(InputStream inputStream, HttpURLConnection httpURLConnection) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Core.writeLogError(TAG, e);
            }
        }
        if (httpURLConnection != null) {
            try {
                httpURLConnection.disconnect();
            } catch (Exception e) {
                Core.writeLogError(TAG, e);
            }
        }
    }


    private String fetchPicture(String s) {

        Matcher m = RssConst.URL_PATTERN.matcher(s);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private Document createDocument(InputStream inputStream) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(inputStream);
    }

    private HttpURLConnection createConnection(String channelUrl) throws Exception {

        URL url = new URL(channelUrl);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(20));
        connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(20));
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        int responce = connection.getResponseCode();
        if (responce != HttpURLConnection.HTTP_OK) {
            throw new Exception("connection responce " + responce);
        }
        return connection;
    }

    @Override
    public void fetchChannel(final String channelUrl, final ChannelListReceiver
            channelListFetching) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<Channel> channelList = new ArrayList<Channel>();
                HttpURLConnection httpURLConnection = null;
                InputStream inputStream = null;
                try {
                    httpURLConnection = createConnection(channelUrl);
                    inputStream = httpURLConnection.getInputStream();
                    Document doc = createDocument(inputStream);
                    NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
                    Element e = (Element) nodeList.item(0);
                    Channel channel = new Channel();
                    channel.setLink(getValue(e, TAG_LINK));
                    channel.setTitle(getValue(e, TAG_TITLE));
                    channel.setUrl(channelUrl);
                    channel.setChannelDescription(getValue(e, TAG_DESRIPTION));
                    channelList.add(channel);
                    channelListFetching.success(channelList);
                } catch (Exception e) {
                    channelListFetching.error(e);
                } finally {
                    closeConnections(inputStream, httpURLConnection);
                }
            }
        });
    }


    @Override
    public void loadImage(final String path, final LoadImageListener loadImageListener, final int
            maxWidth) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = createConnection(path);
                    inputStream = httpURLConnection.getInputStream();
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, o);
                    int scale = 1;
                    while (o.outWidth / scale / 2 >= maxWidth) {
                        scale *= 2;
                    }
                    closeConnections(inputStream, httpURLConnection);
                    o = new BitmapFactory.Options();
                    o.inSampleSize = scale;
                    httpURLConnection = createConnection(path);
                    inputStream = httpURLConnection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream
                            (inputStream);
                    Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream, null, o);

                    loadImageListener.loadSuccess(bitmap);
                } catch (Exception e) {
                    loadImageListener.error(e);
                } finally {
                    closeConnections(inputStream, httpURLConnection);
                }
            }
        });
    }


    public static String createShortMessageBody(String html) {
        String htmlTextStr = Html.fromHtml(html).toString().replace('\n', (char) 32)
                .replace((char) 160, (char) 32).replace((char) 65532, (char) 32).trim();
        ;
        String body = "";
        if (!htmlTextStr.isEmpty()) {
            if (htmlTextStr.length() > 200) {
                body = htmlTextStr.substring(0, 200);
            } else {
                body = htmlTextStr;
            }
        }

        return body;
    }


    String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node
                            .CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }


}
