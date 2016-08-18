package com.bluesnap.android.demoapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;

import com.bluesnap.androidapi.services.PrefsStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.bluesnap.android.demoapp.TestUtils.getCurrentActivity;
import static org.hamcrest.Matchers.containsString;


/**
 * Created by oz on 5/26/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RememberMeDemoFlowTest {
    private static final String TAG = RememberMeDemoFlowTest.class.getSimpleName();
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
        PrefsStorage prefsStorage = new PrefsStorage(getCurrentActivity().getApplicationContext());
        prefsStorage.clear();
    }

    public Double startDemoPurchase() {
        Double demoPurchaseAmount = randomTestValuesGeneretor.randomDemoAppPrice();
        Log.i(TAG, "next purchase " + demoPurchaseAmount);
        //Double demoTaxPrecent = randomTestValuesGeneretor.randomTaxPrecentage();
        tokenProgressBarIR = new VisibleViewIdlingResource(R.id.progressBarMerchant, View.INVISIBLE, "merchant token progress bar");
        transactionMessageIR = new VisibleViewIdlingResource(R.id.transactionResult, View.VISIBLE, "merchant transaction completed text");

        Espresso.registerIdlingResources(tokenProgressBarIR);

        onView(withId(R.id.productPriceEditText)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.productPriceEditText)).check(matches((isDisplayed())));
        //TODO: To test the tax we should calculate the subtotal
        //        onView(withId(R.id.demoTaxEditText)).perform(typeText(demoTaxPrecent.toString()));
        onView(withId(R.id.productPriceEditText)).perform(clearText());
        onView(withId(R.id.productPriceEditText))
                .perform(typeText(demoPurchaseAmount.toString()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.merchantAppSubmitButton)).perform(click());
        return demoPurchaseAmount;
    }

    @Test
    public void valid_cc_transaction_remember_me_first_time() throws InterruptedException {
        startDemoPurchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);
        CardFormTesterCommon.fillInAllFieldsWithValidCard();
        onView(withId(R.id.rememberMeSwitch)).perform(ViewActions.click());
        onView(withId(R.id.buyNowButton)).perform(click());
        finishDemoPurchase();
        onView(withId(R.id.continueShippingButton)).perform(click());

        startDemoPurchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);

        onView(withId(R.id.rememberMeSwitch))
                .perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.rememberMeSwitch)).check(matches(isChecked()));
        onView(withId(R.id.buyNowButton)).perform(click());
        finishDemoPurchase();
    }

    @Test
    public void valid_cc_transaction_remember_me_twice() throws InterruptedException {
        Log.i(TAG, "starting first remember me cycle");
        valid_cc_transaction_remember_me_first_time();
        Log.i(TAG, "completed first remember me cycle");
        onView(withId(R.id.continueShippingButton)).perform(click());
        startDemoPurchase();
        Espresso.unregisterIdlingResources(tokenProgressBarIR);
        onView(withId(R.id.rememberMeSwitch))
                .perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.rememberMeSwitch)).check(matches(isChecked()));
        onView(withId(R.id.buyNowButton)).perform(click());
        finishDemoPurchase();

    }


    public void finishDemoPurchase() {
        Espresso.registerIdlingResources(transactionMessageIR);
        onView(withId(R.id.transactionResult))
                .check(matches(withText(containsString("Transaction Success"))));
        Espresso.unregisterIdlingResources(transactionMessageIR);
    }

}

