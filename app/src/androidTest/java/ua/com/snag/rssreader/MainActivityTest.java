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
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void changeText_sameActivity() {
        onView(withId(R.id.fragment_add_new_feed_url_et)).perform(replaceText("http://kotaku" +
                ".com/vip.xml"));
        onView(withId(R.id.fragment_add_new_feed_bt)).perform(click());
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
