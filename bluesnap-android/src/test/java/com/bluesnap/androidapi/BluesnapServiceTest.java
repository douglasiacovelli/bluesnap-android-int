package com.bluesnap.androidapi;

import android.util.Log;

import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.BluesnapToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.shadows.ShadowLog;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.ProtocolVersion;
import cz.msebera.android.httpclient.message.BasicHttpResponse;

import static junit.framework.Assert.fail;

/**
 * Created by oz on 4/4/16.
 */
//@RunWith(RobolectricTestRunner.class)
//@Config(manifest = Config.DEFAULT_MANIFEST, sdk = 23)
public class BluesnapServiceTest {


    private static final String TAG = BluesnapServiceTest.class.getSimpleName();
    //    @Mock  JsonHttpResponseHandler mockJsonReponseHandler = new JsonHttpResponseHandler();
    @Mock
    RequestHandle mockRequestHandle;
    @Mock
    BluesnapToken mocktoken;
    HttpResponse successResponse = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "{mock RATES}");
    @Mock
    private AsyncHttpClient mockHttpClient;
    @InjectMocks
    private BlueSnapService blueSnapService;
    private String merchantToken;

    //return new JSONObject("{\"baseCurrency\":\"USD\",\"exchangeRate\":[]}");
    @Before
    public void setup() {
        ShadowLog.stream = System.out;
        System.setProperty("robolectric.logging", "stdout");
        MockitoAnnotations.initMocks(this);
        //Mockito.when(mockRequestHandle.isFinished()).thenReturn(true);
        Mockito.when(mocktoken.getUrl()).thenReturn("http://localhost/");
        Mockito.when(mocktoken.getMerchantToken()).thenReturn("somthing-dummyToken_");
//        Mockito.when(mockHttpClient.get(any(String.class),any(JsonHttpResponseHandler.class)))
//                .then();
        //Mockito.doNothing().when(blueSnapService.setup());


    }

    //@Test
    public void testUpdateRatesParsing() throws InterruptedException {
        //blueSnapService = new BlueSnapService();
        //blueSnapService.setup("token-not-mocked_");
        blueSnapService.updateRates(new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "done");
            }

            @Override
            public void onFailure() {
                fail("updaterates failed");
            }
        });

        while (blueSnapService.getRatesArray() == null) {
            Log.d(TAG, "sleeping");
            Thread.sleep(2000);
        }
    }
}