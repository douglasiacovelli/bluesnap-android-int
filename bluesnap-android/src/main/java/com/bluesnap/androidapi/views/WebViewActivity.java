package com.bluesnap.androidapi.views;

import android.app.Activity;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.PaymentResult;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;

public class WebViewActivity extends Activity {

    public static final int PAYPAL_REQUEST_CODE = 1;
    static final String TAG = WebViewActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private WebView webView;
    private TextView textView;
    private String urlRedirect;
    private String textHeader;
    private Boolean javaScriptEnabled;
    private int transactionPendingCounter;
    private String message;
    private String title;
    private BlueSnapService blueSnapService;
    private String procceedURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluesnap_webview);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        webView = (WebView) findViewById(R.id.webView1);
        webView.setWebViewClient(new WebClientProgressBar(progressBar));

        transactionPendingCounter = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            textHeader = extras.getString(getString(R.string.WEBVIEW_STRING));
            urlRedirect = extras.getString(getString(R.string.WEBVIEW_URL));
            javaScriptEnabled = extras.getBoolean(getString(R.string.SET_JAVA_SCRIPT_ENABLED), false);
        }

        if (urlRedirect != null && textHeader != null) {
            textView = (TextView) findViewById(R.id.textHeader1);
            textView.setText(textHeader);
            webView.getSettings().setJavaScriptEnabled(javaScriptEnabled);
            webView.loadUrl(urlRedirect);
        }

        final ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        blueSnapService = BlueSnapService.getInstance();
    }

    public void onPayPalProceedUrl() {
        Log.d(TAG, "onPayPalProceedUrl");
        blueSnapService.retrieveTransactionStatus(new BluesnapServiceCallback() {

            @Override
            public void onSuccess() {
                String transactionStatus = blueSnapService.getTransactionStatus().toUpperCase();
                if ("SUCCESS".equals(transactionStatus)) {
                    UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
                    sanitizer.setAllowUnregisteredParamaters(true);
                    sanitizer.parseUrl(procceedURL);
                    PaymentResult paymentResult = null;
                    try {
                        paymentResult = BlueSnapService.getInstance().getPaymentResult();
                        paymentResult.setPaypalInvoiceId(sanitizer.getValue("INVOICE_ID"));
                    } catch (Exception e) {
                        Log.e(TAG, "paypal state error", e);
                        finishWithAlertDialog("paypal service error", "paypal");
                    }
                    Log.d(TAG, "Payment result from paypal:" + paymentResult);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT, paymentResult);
                    setResult(RESULT_OK, resultIntent);
                    Log.d(TAG, "finishing webview activity");
                    finish();
                } else if ("PENDING".equals(transactionStatus)) {
                    transactionPendingCounter++;
                    if (transactionPendingCounter < 3) {
                        try {
                            Thread.sleep(5000);
                            Log.d(TAG, "retrying paypal transaction");
                            onPayPalProceedUrl();
                        } catch (InterruptedException e) {
                        }
                    } else {
                        message = getString(R.string.SUPPORT_PLEASE)
                                + " "
                                + getString(R.string.SUPPORT);

                        title = getString(R.string.TRANSACTION_FAILED);
                        finishWithAlertDialog(message, title);
                    }

                } else if ("FAIL".equals(transactionStatus)) {
                    message = getString(R.string.SUPPORT_PLEASE)
                            + " "
                            + getString(R.string.SUPPORT);

                    title = getString(R.string.TRANSACTION_FAILED);
                    finishWithAlertDialog(message, title);
                }
            }

            @Override
            public void onFailure() {
                message = getString(R.string.SUPPORT_PLEASE)
                        + " "
                        + getString(R.string.SUPPORT);

                title = getString(R.string.ERROR);
                finishWithAlertDialog(message, title);
            }
        });
    }

    public void onPayPalCancelUrl() {
        finish();
    }

    private void finishWithAlertDialog(String message, String title) {
        BluesnapAlertDialog.setDialog(WebViewActivity.this, message, title, new BluesnapAlertDialog.BluesnapDialogCallback() {
            @Override
            public void setPositiveDialog() {
                finish();
            }

            @Override
            public void setNegativeDialog() {
                finish();
            }
        });
    }

    private class WebClientProgressBar extends WebViewClient {
        private ProgressBar progressBar;

        public WebClientProgressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            Log.d(TAG, "webview loaded " + url);
            if (url.startsWith(Constants.getPaypalProceedUrl())) {
                procceedURL = url;
                blueSnapService.clearPayPalToken();
                Log.d(TAG, "calling onPayPalCancelUrl");

                onPayPalProceedUrl();

                //view.stopLoading();
            } else if (url.startsWith(Constants.getPaypalCancelUrl())) {
                onPayPalCancelUrl();
            }

            progressBar.setVisibility(View.GONE);
        }


    }
}
