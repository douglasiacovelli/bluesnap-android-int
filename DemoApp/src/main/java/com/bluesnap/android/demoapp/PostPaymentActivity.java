package com.bluesnap.android.demoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.models.PaymentResult;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.PrefsStorage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class PostPaymentActivity extends Activity {

    private static final String TAG = PostPaymentActivity.class.getSimpleName();
    private TextView continueShippingView;
    private PrefsStorage prefsStorage;
    private String SHOPPER_ID = "SHOPPER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_payment);
        PaymentResult paymentResult = getIntent().getParcelableExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);
        TextView paymentResultTextView2
                = (TextView) findViewById(R.id.paymentResultTextView2);
        continueShippingView = (TextView) findViewById(R.id.continueShippingButton);
        continueShippingView.setVisibility(View.GONE);
        DecimalFormat decimalFormat = AndroidUtil.getDecimalFormat();
        paymentResultTextView2.setText("Your payment of  " + paymentResult.currencyNameCode + " " + decimalFormat.format(paymentResult.amount) + " has been sent.");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String merchantToken = extras.getString("MERCHANT_TOKEN");
            Log.d(TAG, "Payment Result:\n " + paymentResult.toString());
            if (!paymentResult.rememberUser) {
                createCreditCardTransaction(paymentResult.shopperFirstName, paymentResult.shopperLastName, merchantToken, paymentResult.currencyNameCode, paymentResult.amount);
            } else {
                createCreditCardTransaction(paymentResult.shopperFirstName, paymentResult.shopperLastName, merchantToken, paymentResult.currencyNameCode, paymentResult.amount, true, paymentResult.last4Digits, paymentResult.cardType);
            }
        }
        prefsStorage = new PrefsStorage(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        continueShippingView.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement'
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackFromThankyou(View view) {
        onBackPressed();
    }

    private void createCreditCardTransaction(String firstName, String lastName, String token, String currency, Double amount) {
        createCreditCardTransaction(firstName, lastName, token, currency, amount, false, "", "");
    }

    private void createCreditCardTransaction(String firstName, String lastName, String token, String currency, Double amount, boolean isReturningShopper, String last4Digits, String cardType) {
        String bodyStart = "<card-transaction xmlns=\"http://ws.plimus.com\">" +
                "<card-transaction-type>AUTH_CAPTURE</card-transaction-type>" +
                "<recurring-transaction>ECOMMERCE</recurring-transaction>" +
                "<soft-descriptor>MobileSDK</soft-descriptor>" +
                "<amount>" + amount + "</amount>" +
                "<currency>" + currency + "</currency>";
        String bodyMiddle = "<card-holder-info>" +
                "<first-name>" + firstName + "</first-name>" +
                "<last-name>" + lastName + "</last-name>" +
                "</card-holder-info>";
        String bodyEnd = "</card-transaction>";
        String body = bodyStart;
        String shopperId = prefsStorage.getString(SHOPPER_ID,"");

        if (!isReturningShopper) {
            body += bodyMiddle +
                    "<pf-token>" + token + "</pf-token>" +
                    bodyEnd;
        } else if (!"".equals(shopperId)) {
            body += "<vaulted-shopper-id>" + shopperId + "</vaulted-shopper-id>" +
                    bodyMiddle +
                    " <credit-card>" +
                    "<card-last-four-digits>" + last4Digits + "</card-last-four-digits>" +
                    "<card-type>" + cardType + "</card-type>" +
                    "</credit-card>" +
                    bodyEnd;
        }

        StringEntity entity = new StringEntity(body, "UTF-8");
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setBasicAuth("GCpapi", "Plimus4321");
        Log.d(TAG, "Create transaction body:\n" + body);
        httpClient.post(getApplicationContext(), "https://us-qa-fct03.bluesnap.com/services/2/transactions", entity, "application/xml", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString, throwable);
                //Disabled until server will return a reasonable error
                setDialog(responseString.substring(responseString.indexOf("<error-name>") +
                        "<error-name>".length(), responseString.indexOf("</error-name>")), "Merchant Server");

                setContinueButton();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                prefsStorage.putString(SHOPPER_ID, responseString.substring(responseString.indexOf("<vaulted-shopper-id>") +
                        "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
                Log.d(TAG, responseString);
                setDialog("Transaction Success", "Merchant Server");
                setContinueButton();
            }
        });
    }

    private void setContinueButton() {
        continueShippingView.setVisibility(View.VISIBLE);
        continueShippingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setDialog(String dialogMessage, String dialogTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogMessage).setTitle(dialogTitle);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        try {
            dialog.show();
        } catch (Exception e) {
        }

    }

}

