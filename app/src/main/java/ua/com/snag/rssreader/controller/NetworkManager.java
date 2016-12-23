package ua.com.snag.rssreader.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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


    @Override
    public void fetchRssItemList(final String channelUrl, final RssItemListReceiver
            rssItemListReceiver, boolean orderDesc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                ArrayList<RssItem> rssItemList = new ArrayList<RssItem>();
                try {
                    Document doc = createDocument(channelUrl);
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
                            rssItem.setImageUrl(fetchPicture(description));
                            rssItemList.add(rssItem);
                        }
                    }
                    rssItemListReceiver.success(rssItemList);

                } catch (Exception e) {
                    rssItemListReceiver.error(e);
                }
            }
        }).start();

    }


    private String fetchPicture(String s) {

        Matcher m = RssConst.URL_PATTERN.matcher(s);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private Document createDocument(String channelUrl) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(createConnection(channelUrl));
    }

    private InputStream createConnection(String channelUrl) throws Exception {
        Core.writeLog(TAG, "channelUrl " + channelUrl);
        URL url = new URL(channelUrl);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(20));
        connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(20));
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        int response = connection.getResponseCode();
        return connection.getInputStream();
    }

    @Override
    public void fetchChannel(final String channelUrl, final ChannelListReceiver
            channelListFetching) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Channel> channelList = new ArrayList<Channel>();
                try {
                    Document doc = createDocument(channelUrl);
                    NodeList nodeList = doc.getElementsByTagName(TAG_CHANNEL);
                    Element e = (Element) nodeList.item(0);
                    Channel channel = new Channel();
                    channel.setLink(getValue(e, TAG_LINK));
                    Core.writeLog(TAG, "fetch channel.getLink() " + channel.getLink());
                    channel.setTitle(getValue(e, TAG_TITLE));
                    channel.setUrl(channelUrl);
                    channel.setChannelDescription(getValue(e, TAG_DESRIPTION));
                    channelList.add(channel);
                    channelListFetching.success(channelList);
                } catch (Exception e) {
                    channelListFetching.error(e);
                }
            }
        }).start();
    }


    @Override
    public void loadImage(final String path, final LoadImageListener loadImageListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = createConnection(path);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream
                            (inputStream);
                    Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                    loadImageListener.loadSuccess(bitmap);
                } catch (Exception e) {
                    loadImageListener.error(e);
                }
            }
        }).start();
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
