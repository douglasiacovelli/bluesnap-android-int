package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.bluesnap.androidapi.services.AndroidUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;


/**
 * Created by oz on 5/26/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DemoFlowTest {
    @Rule
    public ActivityTestRule<DemoMainActivity> mActivityRule = new ActivityTestRule<>(
            DemoMainActivity.class);


    private RandomTestValuesGenerator randomTestValuesGeneretor;
    private IdlingResource tokenProgressBarIR;
    private IdlingResource transactionMessageIR;

    @After
    public void keepRunning() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Before
    public void setup() {
        randomTestValuesGeneretor = new RandomTestValuesGenerator();
        IdlingPolicies.setMasterPolicyTimeout(120, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(100, TimeUnit.SECONDS);
    }

    public Double startDemoPurchase() {
        Double demoPurchaseAmount = randomTestValuesGeneretor.randomDemoAppPrice();
        tokenProgressBarIR = new VisibleViewIdlingResource(R.id.progressBarMerchant, View.INVISIBLE);
        transactionMessageIR = new VisibleViewIdlingResource(R.id.transactionResult, View.VISIBLE);

        Espresso.registerIdlingResources(tokenProgressBarIR);

        onView(withId(R.id.productPriceEditText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.productPriceEditText)).check(matches((isDisplayed())));
        onView(withId(R.id.productPriceEditText))
                .perform(typeText(demoPurchaseAmount.toString()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.merchantAppSubmitButton)).perform(click());
        return demoPurchaseAmount;
    }

    @Test
    public void valid_CC_without_Shipping_Transaction_Test() throws InterruptedException {
        startDemoPurchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);
        CardFormTesterCommon.fillInAllFieldsWithValidCard();
        onView(withId(R.id.buyNowButton)).perform(click());
        finishDemoPurchase();
    }

    public void finishDemoPurchase() {
        Espresso.registerIdlingResources(transactionMessageIR);
        onView(withId(R.id.transactionResult))
                .check(matches(withText(containsString("Transaction Success"))));
        Espresso.unregisterIdlingResources(transactionMessageIR);
    }

    @Test
    public void changeCurrencyOnceCheck() throws InterruptedException {
        Double startDemoPurchaseAmount = startDemoPurchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);
        onView(withId(R.id.buyNowButton)).check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));
        CardFormTesterCommon.fillInAllFieldsWithValidCard();
        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("CAD"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("CAD")))));

        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("ILS"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("ILS")))));

        onView(withId(R.id.hamburger_button)).perform(click());
        onView(withText(containsString("Currency"))).perform(click());
        onData(hasToString(containsString("USD"))).inAdapterView(withId(R.id.currency_list_view)).perform(click());
        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getCurrencySymbol("USD")))));

        onView(withId(R.id.buyNowButton))
                .check(matches(withText(containsString(AndroidUtil.getDecimalFormat().format(startDemoPurchaseAmount.toString())))));


        onView(withId(R.id.buyNowButton)).perform(click());
        finishDemoPurchase();

    }
}

