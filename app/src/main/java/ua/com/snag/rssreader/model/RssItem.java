package ua.com.snag.rssreader.model;

/**
 * Created by holod on 21.12.16.
 */

public class RssItem {
    public static final String TABLE_NAME = "_rss_items";
    public static final String ID = "_id";
    public static final String CHANNEL = "_channel";
    public static final String LINK = "_link";
    public static final String TITLE = "_title";
    public static final String PUB_DATE = "_pub_date";
    public static final String SHORT_DESCRIPTION = "_short_description";
    public static final String IMAGE_URL = "_image_url";
    public static final String IMAGE_PATH = "_image_path";

    private String channel, link, title, shortDescription, pubDate, imageUrl, imagePath;
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
