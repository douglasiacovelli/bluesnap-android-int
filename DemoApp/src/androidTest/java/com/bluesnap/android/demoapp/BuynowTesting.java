package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.lang.reflect.Field;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BufferedHeader;

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
        blueSnapService.setup("463448920b4fd76c4c83a35fb0b22cdd6f11ebeca91a00cd7416bb7b28886975_", getApplicationContext());
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

    //@Test
    public void testRatesServiceInit() {

        serviceInstance = BlueSnapService.getInstance();
        SyncHttpClient httpClient = new SyncHttpClient();

        httpClient.setBasicAuth("GCpapi", "Plimus4321");
        httpClient.post("https://us-qa-fct03.bluesnap.com/services/2/payment-fields-tokens", new TextHttpResponseHandler() {

            public String merchantToken;

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TAG", responseString, throwable);
                Assert.fail("No Token: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                for (Header hr : headers) {
                    BufferedHeader bufferedHeader = (BufferedHeader) hr;
                    if (bufferedHeader.getName().equals("Location")) {
                        String path = bufferedHeader.getValue();
                        merchantToken = path.substring(path.lastIndexOf('/') + 1);
                    }
                }
                serviceInstance.setup(merchantToken, getApplicationContext());
                serviceInstance.updateRates(new BluesnapServiceCallback() {
                    @Override
                    public void onSuccess() {
                        //TODO: test
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail("failed to get rates");
                    }
                });

            }
        });

        while (serviceInstance.getRatesArray() == null) {
            Log.d("TAG", "Waiting for rates");
        }

    }
//    @Test
//    public  void subjectActivityShouldReturnCorrectActivityResult() {
//        runActivityResultTest(new ResultTestActivity.ActivityResultTest() {
//            @Override
//            public Intent getSubjectIntent() {
//                return intent;
//            }
//
//            @Override
//            public void triggerActivityResult() {
//                CardFormTesterCommon.fillInAllFieldsWithValidCard();
//                onView(withId(R.id.buyNowButton)).perform(click());
//            }
//
//            @Override
//            public Matcher<ResultTestActivity> getActivityResultMatcher() {
//                return receivedExpectedResult(is(Activity.RESULT_OK),
//                        IntentMatchers.hasExtra("EXTRA_PAYMENT_RESULT1", "1"));
//            }
//        });
//    }

}

