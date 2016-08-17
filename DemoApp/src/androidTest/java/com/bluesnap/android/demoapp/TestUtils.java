package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.os.Looper;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static org.hamcrest.CoreMatchers.is;

public class TestUtils {
    public static Activity getCurrentActivity() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return getCurrentActivityOnMainThread();
        } else {
            final Activity[] topActivity = new Activity[1];
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    topActivity[0] = getCurrentActivityOnMainThread();
                }
            });
            return topActivity[0];
        }
    }

    private static Activity getCurrentActivityOnMainThread() {
        ActivityLifecycleMonitor registry = ActivityLifecycleMonitorRegistry.getInstance();
        Collection<Activity> activities = registry.getActivitiesInStage(Stage.RESUMED);
        return activities.iterator().hasNext() ? activities.iterator().next() : null;
    }

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value. Note: View's
     * text property is never null. If you setText(null) it will still be "". Do not use null
     * matcher.
     *
     * @param integerMatcher {@link Matcher} of {@link String} with text to match
     */
    public static Matcher<View> withCurrentTextColor(final Matcher<Integer> integerMatcher) {
        checkNotNull(integerMatcher);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
                integerMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                return integerMatcher.matches(textView.getCurrentTextColor());
            }
        };
    }

    /**
     * Returns a matcher that matches {@link TextView} based on it's text property value. Note:
     * View's Sugar for withTextColor(is("string")).
     */
    public static Matcher<View> withCurrentTextColor(int color) {
        return withCurrentTextColor(is(color));
    }

}