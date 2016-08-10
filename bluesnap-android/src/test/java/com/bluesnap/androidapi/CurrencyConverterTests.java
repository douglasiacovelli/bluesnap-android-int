package com.bluesnap.androidapi;

import android.util.Log;

import com.bluesnap.androidapi.models.ExchangeRate;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import junit.framework.Assert;

import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BufferedHeader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created by oz on 4/4/16.
 */
//@RunWith(RobolectricTestRunner.class)
public class CurrencyConverterTests {


    private static final String TAG = CurrencyConverterTests.class.getSimpleName();
    private BlueSnapService blueSnapService;
    private String merchantToken;

    public CurrencyConverterTests() {
        ShadowLog.stream = System.out;
        System.setProperty("robolectric.logging", "stdout");
    }

    @Before
    public void testServiceRatesArray() throws InterruptedException {
        ShadowLog.stream = System.out;
        System.setProperty("robolectric.logging", "stdout");

        AsyncHttpClient httpClient = new AsyncHttpClient();

        httpClient.setBasicAuth("GCpapi", "Plimus4321");
        httpClient.post("https://us-qa-fct03.bluesnap.com/services/2/payment-fields-tokens", new TextHttpResponseHandler() {


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
                super.onFailure(statusCode, headers, responseBytes, throwable);
                fail("no token");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, responseString, throwable);
                fail("no token");
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
            }
        });

        ShadowApplication.runBackgroundTasks();


    }

    //@Test(timeout = 12000)
    public void TestConversionService() throws InterruptedException {
        while (merchantToken == null) {
            Thread.sleep(2000);
            ShadowLog.i(TAG, "Waiting for token");
            ShadowApplication.runBackgroundTasks();
            Robolectric.flushBackgroundThreadScheduler(); //from 3.0


        }
        blueSnapService = BlueSnapService.getInstance();
        blueSnapService.setup(merchantToken);
        blueSnapService.updateRates(new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                ShadowLog.d(TAG, "Got rates callback");
                ArrayList<ExchangeRate> ratesArray = blueSnapService.getRatesArray();
                Assert.assertNotNull(ratesArray.get(0));
            }

            @Override
            public void onFailure() {
                fail("failed to get rates");
            }
        });
        while (blueSnapService.getRatesArray() == null) {
            Thread.sleep(2000);
            ShadowLog.i(TAG, "Waiting for rates");

        }
        Double usdPrice = 30.5;
        Double ILSPrice = blueSnapService.convertPrice(usdPrice, "USD", "ILS");
        Double reconvertedUSDPrice = blueSnapService.convertPrice(ILSPrice, "ILS", "USD");
        assertEquals(usdPrice, reconvertedUSDPrice);

    }
}