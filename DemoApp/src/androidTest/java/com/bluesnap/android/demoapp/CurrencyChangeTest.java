package com.bluesnap.android.demoapp;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.views.CustomListAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsEqual.equalTo;


/**
 * Created by oz on 5/26/16.
 */
//@RunWith(AndroidJUnit4.class)
@LargeTest
public class CurrencyChangeTest {
    @Rule
    public ActivityTestRule<BluesnapCheckoutActivity> mActivityRule = new ActivityTestRule<>(
            BluesnapCheckoutActivity.class, false, false);
    private BluesnapCheckoutActivity mActivity;

    @After
    public void keepRunning() throws InterruptedException {
        //while (true) { Thread.sleep(2000); } //Remove this
    }

    @Before
    public void setup() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(23.4);
        Intent intent = new Intent();
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST, paymentRequest);
        paymentRequest.setCurrencyNameCode("USD");
        paymentRequest.setShippingRequired(false);
        mActivityRule.launchActivity(intent);
    }


    //@Test
    public void changeCurrencyOnceCheck() throws InterruptedException {
        onView(withId(R.id.hamburger_button)).perform(click());

        onData(allOf(is(instanceOf(CustomListAdapter.class)), hasEntry(equalTo("USD"), is("United States Dollar")))).perform(click());

        onView(withId(R.id.buyNowButton)).perform(click());

    }

}

