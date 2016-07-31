package com.bluesnap.androidapi;

import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Before;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.httpclient.FakeHttp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;


//@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AsyncTester {

    protected static final int HTTP_OK = 200;

    //    protected ApiCallback mCallback;
    protected AsyncHttpClient mHttpClient = new AsyncHttpClient();
    private ProtocolVersion mHttpVersion;
    private String absoluteUrl;
    private Button button;


    @Before
    public void setUp() {
        BlockingQueue<Runnable> queue = new SynchronousQueue<Runnable>();
        TestExecutorService pool = new TestExecutorService(10, 10, 10, TimeUnit.MILLISECONDS, queue);
        mHttpVersion = new ProtocolVersion("HTTP", 1, 1);
        mHttpClient.setThreadPool(pool);
    }

    /**
     * Mock a pending response.
     *
     * @param url
     * @param response the response to return, when the request is done.
     */
    protected void addPendingResponse(String url, String response) {
        HttpResponse successResponse = new BasicHttpResponse(mHttpVersion, 200, response);
        FakeHttp.setDefaultHttpResponse(400, null);
        FakeHttp.addHttpResponseRule(absoluteUrl, successResponse);
    }

    /**
     * Mock a pending response. {@code ApiMock.mockSuccess()} is returned as a
     * response.
     *
     * @param url
     */
    protected void addPendingResponse(String url) {
//        addPendingResponse(url, ApiMock.mockSuccessToken());
    }

//    @Test
//    public void onCreateShouldInflateTheMenu() {
//        BluesnapCheckoutActivity bluesnapCheckoutActivity = Robolectric.setupActivity(BluesnapCheckoutActivity.class);
//        button = (Button) bluesnapCheckoutActivity.findViewById(R.id.buyNowButton);
//        button.performClick();
//        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
//    }

}