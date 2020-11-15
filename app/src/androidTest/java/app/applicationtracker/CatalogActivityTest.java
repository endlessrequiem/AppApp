package app.applicationtracker;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anyOf;


@RunWith(AndroidJUnit4.class)
public class CatalogActivityTest {

    @Rule
    public ActivityTestRule<CatalogActivity> mActivityTestRule = new ActivityTestRule<>(CatalogActivity.class);


    @Test
    public void FABtest() {
        onView((withId(R.id.fab)))
                .perform(click());

    }

    @Test
    public void menuTest() {
        try {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        } catch (Exception e) {
            //This is normal. Maybe we dont have overflow menu.
        }
        onView(anyOf(withText(R.string.action_insert_dummy_data), withId(R.id.action_insert_dummy_data))).perform(click());

    }

}