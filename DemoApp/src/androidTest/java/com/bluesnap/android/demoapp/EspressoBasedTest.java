package com.bluesnap.android.demoapp;

import android.content.Context;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.util.Base64;

import com.bluesnap.androidapi.services.PrefsStorage;

import org.junit.Before;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_PASS;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_TOKEN_CREATION;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_URL;
import static com.bluesnap.android.demoapp.DemoToken.SANDBOX_USER;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.containsString;

/**
 *
 */
public class EspressoBasedTest {
    public String merchantToken;
    protected RandomTestValuesGenerator randomTestValuesGeneretor;
    protected IdlingResource tokenProgressBarIR;
    protected IdlingResource transactionMessageIR;

    @Before
    public void setup() throws IOException {
        randomTestValuesGeneretor = new RandomTestValuesGenerator();
        IdlingPolicies.setMasterPolicyTimeout(400, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(400, TimeUnit.SECONDS);

        URL myURL = new URL(SANDBOX_URL + SANDBOX_TOKEN_CREATION);
        HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
        String userCredentials = SANDBOX_USER + ":" + SANDBOX_PASS;
        String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), 0));
        myURLConnection.setRequestProperty("Authorization", basicAuth);
        myURLConnection.setRequestMethod("POST");
        myURLConnection.connect();
        int responseCode = myURLConnection.getResponseCode();
        String locationHeader = myURLConnection.getHeaderField("Location");
        merchantToken = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);

    }

    public void clearPrefs(Context context) {
        PrefsStorage prefsStorage = new PrefsStorage(context);
        prefsStorage.clear();
    }

    public void checkToken() {
        try {
            onView(withText(containsString("Cannot obtain token"))).check(matches(isDisplayed()));
            fail("No token from server");
        } catch (NoMatchingViewException e) {
            //view not displayed logic
        }
    }

    //@After
    public void detectIfNoToken() {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);
        checkToken();

    }
}
