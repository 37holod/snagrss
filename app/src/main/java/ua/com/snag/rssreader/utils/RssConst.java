package ua.com.snag.rssreader.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by holod on 22.12.16.
 */

public class RssConst {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
    public static Pattern URL_PATTERN = Pattern.compile("(https?:\\/\\/[^ ]*\\." +
            "(?:png|jpg|jpeg|gif|png|svg))");
}
