package com.bluesnap.android.demoapp;

import android.support.test.espresso.action.ViewActions;
import android.util.Log;

import com.bluesnap.androidapi.models.Card;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import cz.msebera.android.httpclient.Header;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by oz on 5/30/16.
 */
public class CardFormTesterCommon {

    public static void fillInAllFieldsWithValidCard() {
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.emailEditText)).perform(typeText("John Doe"));
        onView(withId(R.id.expDateEditText)).perform(typeText("11"));
        onView(withId(R.id.expDateEditText)).perform(typeText("2011"));
        onView(withId(R.id.cvvEditText)).perform(typeText("123")).perform(ViewActions.closeSoftKeyboard());

    }

    public static void fillInShippingDetails() {
        onView(withId(R.id.shippingNameEditText)).perform(typeText("John Doe"));
        onView(withId(R.id.shippingAddressLine)).perform(typeText("9 Baker street"));
        onView(withId(R.id.shippingCityEditText)).perform(typeText("London"));
        onView(withId(R.id.shippingStateEditText)).perform(typeText("UK")).perform(ViewActions.closeSoftKeyboard());

    }

    public static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    public static String invalidCardNumberGeneratorTest() {
        return "557275888112233";
    }

    public static String getMockMerchantToken() {


        AsyncHttpClient httpClient = new AsyncHttpClient(true, 80, 443);
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            trustStore.load(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        MySSLSocketFactory sf = null;
        try {
            sf = new MySSLSocketFactory(trustStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        httpClient.setSSLSocketFactory(sf);
        httpClient.setMaxRetriesAndTimeout(1, 4000);
        httpClient.setConnectTimeout(4000);

        //httpClient.setThreadPool(TestExecutorService.);
        httpClient.post("https://us-qa-fct03.bluesnap.com/services/2/sdk-tokens", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("TEST", "res" + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("TEST", "fails" + statusCode);
            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
            }
        });
        return null;
    }

    private Card cardGeneretaor() {
        Card card = new Card();
        card.setNumber("5572758886015288");
        card.setCVC("123");
        return card;

    }
}
