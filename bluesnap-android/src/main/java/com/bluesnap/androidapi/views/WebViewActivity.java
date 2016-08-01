package com.bluesnap.androidapi.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;

public class WebViewActivity extends Activity {

    static final String TAG = String.valueOf(ExpressCheckoutFragment.class.getPackage());
    private ProgressBar progressBar;
    private WebView webView;
    private TextView textView;
    private String urlRedirect;
    private String textHeader;
    private Boolean javaScriptEnabled;
    private int transactionPendingCounter;
    private String message;
    private String title;

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

    }

    public void onPayPalProceedUrl() {
        BlueSnapService.getInstance().retrieveTransactionStatus(new BluesnapServiceCallback() {

            @Override
            public void onSuccess() {
                String transactionStatus = BlueSnapService.getTransactionStatus().toUpperCase();

                if (transactionStatus.equals("SUCCESS")) {
                    // Todo implement transaction transfer
                    //example: https://sandbox.bluesnap.com/jsp/dev_scripts/iframeCheck/pay_pal_proceed.html?ERROR=0&INVOICE_ID=1017059422&PAYPAL_TRANSACTION_ID=8VC67186CA344511P&SELLER_ORDER_ID=null

                } else if (transactionStatus.equals("PENDING")) {
                    transactionPendingCounter++;
                    if (transactionPendingCounter < 3) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        onPayPalProceedUrl();
                                    }
                                }, 1500);
                    } else {
                        message = getString(R.string.SUPPORT_PLEASE)
                                + " "
                                + getString(R.string.SUPPORT);

                        title = getString(R.string.TRANSACTION_FAILED);
                        finishWithAlertDialog(message, title);
                    }


                } else if (transactionStatus.equals("FAIL")) {
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
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            super.onPageFinished(view, url);
            if (url.startsWith(Constants.getPaypalProceedUrl())) {
                BlueSnapService.clearPayPalToken();
                onPayPalProceedUrl();
            } else if (url.startsWith(Constants.getPaypalCancelUrl())) {
                onPayPalCancelUrl();
            }

            progressBar.setVisibility(View.GONE);
        }
    }
}
