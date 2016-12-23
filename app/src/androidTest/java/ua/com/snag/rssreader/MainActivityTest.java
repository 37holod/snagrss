package ua.com.snag.rssreader;

/**
 * Created by holod on 23.12.16.
 */

import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ua.com.snag.rssreader.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void onClick() throws Exception {

        onView(withId(R.id.fragment_add_new_feed_url_et)).perform(typeText("http://censor.net" +
                ".ua/includes/news_ru.xml"));
        onView(withId(R.id.fragment_add_new_feed_bt)).perform(click());
    }
}
