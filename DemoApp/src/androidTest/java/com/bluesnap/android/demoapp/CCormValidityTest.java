package com.bluesnap.android.demoapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.PaymentRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.bluesnap.android.demoapp.CardFormTesterCommon.cardNumberGeneratorTest;
import static com.bluesnap.android.demoapp.CardFormTesterCommon.invalidCardNumberGeneratorTest;
import static org.hamcrest.Matchers.not;


/**
 * Created by oz on 5/26/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CCormValidityTest {
    @Rule
    public ActivityTestRule<BluesnapCheckoutActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCheckoutActivity.class, false, false);
    private BluesnapCheckoutActivity mActivity;

    @After
    public void keepRunning() throws InterruptedException {
//        while (true) { Thread.sleep(2000); } //Remove this
    }

    @Before
    public void setup() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(23.4);
        Intent intent = new Intent();
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST, paymentRequest);
        paymentRequest.setCurrencyNameCode("USD");
        paymentRequest.setShippingRequired(false);
        paymentRequest.allowRememberUser(false);
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void invalidCardMessageCheck() throws InterruptedException {
        //Test validation of invalid number
        onView(withId(R.id.invaildCreditCardMessageTextView)).check(matches(not(ViewMatchers.isDisplayed())));
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(invalidCardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.invaildCreditCardMessageTextView)).check(matches(ViewMatchers.isDisplayed()));

        //Clear the invalid number
        onView(withId(R.id.creditCardNumberEditText)).perform(clearText());

        //Put a valid number
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.invaildCreditCardMessageTextView)).check(matches(not(ViewMatchers.isDisplayed())));
    }


    @Test
    public void dateFieldValidationCheck() throws InterruptedException {
        //Test validation of invalid Month (56)
        onView(withId(R.id.expDateLabelTextView)).check(matches(not(TestUtils.withCurrentTextColor(Color.RED))));
        onView(withId(R.id.expDateEditText))
                .perform(typeText("56 44"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.expDateLabelTextView)).check(matches((TestUtils.withCurrentTextColor(Color.RED))));
        onView(withId(R.id.expDateEditText))
                .perform(clearText());

        //Now enter a valid month
        onView(withId(R.id.expDateEditText))
                .perform(typeText("12 36"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.expDateLabelTextView)).check(matches(not(TestUtils.withCurrentTextColor(Color.RED))));
    }

    @Test
    public void dateFieldPastCheck() throws InterruptedException {
        onView(withId(R.id.expDateLabelTextView)).check(matches(not(TestUtils.withCurrentTextColor(Color.RED))));
        onView(withId(R.id.expDateEditText))
                .perform(typeText("11 05"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.expDateLabelTextView)).check(matches((TestUtils.withCurrentTextColor(Color.RED))));
        onView(withId(R.id.expDateEditText))
                .perform(clearText());

        //Now enter a valid month
        onView(withId(R.id.expDateEditText))
                .perform(typeText("12 30"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.buyNowButton)).perform(click());
        onView(withId(R.id.expDateLabelTextView)).check(matches(not(TestUtils.withCurrentTextColor(Color.RED))));
    }
}

