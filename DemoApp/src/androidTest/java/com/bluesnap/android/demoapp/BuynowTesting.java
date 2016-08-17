package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.services.BlueSnapService;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.lang.reflect.Field;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Created by oz on 5/26/16.
 */
//@RunWith(AndroidJUnit4.class)
@LargeTest
public class BuynowTesting {
    @Rule
    public ActivityTestRule<BluesnapCheckoutActivity> mActivityRule = new ActivityTestRule<BluesnapCheckoutActivity>(
            BluesnapCheckoutActivity.class, false, false);
    private BluesnapCheckoutActivity mActivity;
    private Intent intent;
    private BlueSnapService serviceInstance;

    @After
    public void keepRunning() throws InterruptedException {
//        while (true) {
//            Thread.sleep(2000);
//        } //Remove this
    }

    @Before
    public void setup() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(23.4);
        intent = new Intent();
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST, paymentRequest);
        intent.putExtra(BluesnapCheckoutActivity.MERCHANT_TOKEN, "463448920b4fd76c4c83a35fb0b22cdd6f11ebeca91a00cd7416bb7b28886975_");
        BlueSnapService blueSnapService = BlueSnapService.getInstance();
        blueSnapService.setup("463448920b4fd76c4c83a35fb0b22cdd6f11ebeca91a00cd7416bb7b28886975_");
        paymentRequest.setCurrencyNameCode("USD");
        paymentRequest.setShippingRequired(false);
        mActivity = mActivityRule.launchActivity(intent);

    }


    //@Test
    public void testPaymentResult() throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        CardFormTesterCommon.fillInAllFieldsWithValidCard();
        Thread.sleep(1000);
        onView(withId(R.id.buyNowButton)).perform(click());
        //Matcher<Intent> intentMatcher = toPackage(mActivity.getPackageName());
        //Assert.assertNotNull(intentMatcher);
        //intended(intentMatcher);
        Field f = Activity.class.getDeclaredField("mResultCode"); //NoSuchFieldException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            while (!mActivityRule.getActivity().isDestroyed()) {
                Thread.sleep(100);
            }
        }
        f.setAccessible(true);
        int mResultCode = f.getInt(mActivityRule.getActivity());
        Assert.assertTrue("The result code is not ok. ", mResultCode == Activity.RESULT_OK);

    }

}

