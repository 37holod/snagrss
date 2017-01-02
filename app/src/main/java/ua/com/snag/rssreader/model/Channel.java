package ua.com.snag.rssreader.model;

/**
 * Created by holod on 21.12.16.
 */

public class Channel {
    public static final String TABLE_NAME = "_channels";
    public static final String ID = "_id";
    public static final String URL = "_url";
    public static final String TITLE = "_name";
    public static final String LINK = "_link";
    public static final String CHANNEL_DESCRIPTION = "_channel_description";

    private String url, title, link, channelDescription;

    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        return url.equals(channel.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
