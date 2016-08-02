package com.bluesnap.androidapi.views;

import android.app.Activity;
import android.net.UrlQuerySanitizer;
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
        blueSnapService.retrieveTransactionStatus(new BluesnapServiceCallback() {

            @Override
            public void onSuccess() {
                String transactionStatus = blueSnapService.getTransactionStatus().toUpperCase();

                if ("SUCCESS".equals(transactionStatus)) {

                    UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
                    sanitizer.setAllowUnregisteredParamaters(true);
                    sanitizer.parseUrl(procceedURL);
                    // ToDo
                    // PaymentResult paymentResult = BlueSnapService.getInstance().getPaymentResult();
                    // paymentResult.invoiceId4PayPal = Integer.getInteger(sanitizer.getValue("INVOICE_ID"));

                    finishWithAlertDialog(procceedURL, sanitizer.getValue("INVOICE_ID"));


                } else if ("PENDING".equals(transactionStatus)) {
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
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.startsWith(Constants.getPaypalProdUrl())
                    && !url.startsWith(Constants.getPaypalSandUrl())
                    && !url.startsWith(Constants.getPaypalProceedUrl())
                    && !url.startsWith(Constants.getPaypalCancelUrl())) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            //super.onPageFinished(view, url);
            if (url.startsWith(Constants.getPaypalProceedUrl())) {
                procceedURL = url;
                blueSnapService.clearPayPalToken();
                onPayPalProceedUrl();
            } else if (url.startsWith(Constants.getPaypalCancelUrl())) {
                onPayPalCancelUrl();
            }

            progressBar.setVisibility(View.GONE);
        }


    }
}
