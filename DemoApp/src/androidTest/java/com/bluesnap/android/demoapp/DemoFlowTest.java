package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;


/**
 * Created by oz on 5/26/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DemoFlowTest {
    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityRule = new ActivityTestRule<>(
            DemoMainActivity.class);


    private DemoMainActivity mActivity;
    private RandomTestValuesGeneretor randomTestValuesGeneretor;

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() {
        randomTestValuesGeneretor = new RandomTestValuesGeneretor();
        IdlingPolicies.setMasterPolicyTimeout(120, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(100, TimeUnit.SECONDS);
    }

    @Test
    public void valid_CC_without_Shipping_Transaction_Test() throws InterruptedException {

        Double demoPurchaseAmount = randomTestValuesGeneretor.randomDemoAppPrice();
        IdlingResource tokenProgressBarIR = new VisibleViewIdlingResource(R.id.progressBarMerchant, View.INVISIBLE);
        IdlingResource transactionMessageIR = new VisibleViewIdlingResource(R.id.transactionResult, View.VISIBLE);

        Espresso.registerIdlingResources(tokenProgressBarIR);

        onView(withId(R.id.productPriceEditText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.productPriceEditText)).check(matches((isDisplayed())));
        onView(withId(R.id.productPriceEditText))
                .perform(typeText(demoPurchaseAmount.toString()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.merchantAppSubmitButton)).perform(click());
        CardFormTesterCommon.fillInAllFieldsWithValidCard();
        onView(withId(R.id.buyNowButton)).perform(click());

        Espresso.unregisterIdlingResources(tokenProgressBarIR);
        Espresso.registerIdlingResources(transactionMessageIR);
        onView(withId(R.id.transactionResult)).check(matches(withText(containsString("Transaction Success"))));
        Espresso.unregisterIdlingResources(transactionMessageIR);
    }


}

