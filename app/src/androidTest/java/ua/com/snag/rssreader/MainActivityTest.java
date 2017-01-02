package ua.com.snag.rssreader;

/**
 * Created by holod on 23.12.16.
 */

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ua.com.snag.rssreader.activities.MainActivity;
import ua.com.snag.rssreader.test.IdlingResourceImpl;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MainActivityTest {
    private IdlingResourceImpl mIdlingResource;
    private String[] arr = new String[]{
            "http://rss.cnn.com/rss/cnn_topstories.rss",
            "http://feeds.nytimes.com/nyt/rss/HomePage",
            "http://hosted.ap.org/lineups/USHEADS-rss_2.0.xml?SITE=RANDOM&SECTION=HOME",
            "http://rssfeeds.usatoday.com/usatoday-NewsTopStories",
            "http://www.npr.org/rss/rss.php?id=1001",
            "http://feeds.reuters.com/reuters/topNews",
            "http://newsrss.bbc.co.uk/rss/newsonline_world_edition/americas/rss.xml"
    };
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
    }

    @Test
    public void addSomeFeeds() {
        for (String link : arr) {
            Espresso.registerIdlingResources(mIdlingResource);
            mActivityRule.getActivity().addNewFeedFragment();
            onView(withId(R.id.fragment_add_new_feed_url_et)).perform(replaceText(link));
            onView(withId(R.id.fragment_add_new_feed_bt)).perform(click());
            if (mIdlingResource != null) {
                Espresso.unregisterIdlingResources(mIdlingResource);
            }
        }

    }

}