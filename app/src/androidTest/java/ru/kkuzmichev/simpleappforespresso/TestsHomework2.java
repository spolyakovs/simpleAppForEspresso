package ru.kkuzmichev.simpleappforespresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.Allure;
import io.qameta.allure.kotlin.junit4.DisplayName;

@RunWith(AllureAndroidJUnit4.class)
public class TestsHomework2 {

    @Before
    public void registerIdlingResources() {
        IdlingRegistry.getInstance().register(IdlingResources.idlingResource);
    }

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            String className = description.getClassName();
            className = className.substring(className.lastIndexOf('.') + 1);
            String methodName = description.getMethodName();
            takeScreenshot(className + "#" + methodName);
        }
    };

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    @DisplayName("Check intent")
    public void checkIntent() {
        String url = "https://google.com";
        ViewInteraction options = onView(
                withContentDescription("More options")
        );
        options.perform(click());
        ViewInteraction settings = onView(
                withId(R.id.title)
        );
        Intents.init();
        settings.perform(click());
        intended(hasData(url));
        intended(hasAction(Intent.ACTION_VIEW));
        Intents.release();
    }

    @Test
    @DisplayName("Check List")
    public void checkList() {
        ViewInteraction navigation = onView(
                withContentDescription("Open navigation drawer")
        );
        navigation.check(matches(isDisplayed()));
        navigation.perform(click());

        ViewInteraction gallery = onView(withText("Gallery"));
        gallery.check(matches(isDisplayed()));
        gallery.perform(click());

        ViewInteraction recyclerView = onView(withId(R.id.recycle_view));
        recyclerView.check(CustomViewAssertion.isRecyclerView());
        recyclerView.check(matches(CustomViewMatcher.recyclerViewSizeMatcher(10)));

        ViewInteraction itemInRecyclerView = onView(allOf(withId(R.id.item_number), withText("1")));
        itemInRecyclerView.check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(IdlingResources.idlingResource);
    }

    private void takeScreenshot(String name) {
        File path = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/screenshots/" );
        if (!path.exists()) {
            path.mkdirs();
        }
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        String filename = name + ".png";
        device.takeScreenshot(new File(path, filename));
        try {
            Allure.attachment(filename, new FileInputStream(new File(path, filename)));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
