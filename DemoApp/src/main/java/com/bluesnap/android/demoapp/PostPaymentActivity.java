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
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.PrefsStorage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class PostPaymentActivity extends Activity {

    private static final String TAG = PostPaymentActivity.class.getSimpleName();
    private TextView continueShippingView;
    private Transactions transactions;

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

            transactions = Transactions.getInstance();
            transactions.setContext(this);
            if (paymentResult.isReturningTransaction()) {
                transactions.createCreditCardTransaction(paymentResult.shopperFirstName, paymentResult.shopperLastName, merchantToken, paymentResult.currencyNameCode, paymentResult.amount, true, paymentResult.last4Digits, paymentResult.cardType, new BluesnapServiceCallback() {
                    @Override
                    public void onSuccess() {
                        setDialog(transactions.getMessage(), transactions.getTitle());
                        setContinueButton();
                    }

                    @Override
                    public void onFailure() {
                        setDialog(transactions.getMessage(), transactions.getTitle());
                        setContinueButton();
                    }
                });
            } else {
                transactions.createCreditCardTransaction(paymentResult.shopperFirstName, paymentResult.shopperLastName, merchantToken, paymentResult.currencyNameCode, paymentResult.amount, new BluesnapServiceCallback() {
                    @Override
                    public void onSuccess() {
                        setDialog(transactions.getMessage(), transactions.getTitle());
                        setContinueButton();
                    }

                    @Override
                    public void onFailure() {
                        setDialog(transactions.getMessage(), transactions.getTitle());
                        setContinueButton();
                    }
                });
            }
        }
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

    public void setContinueButton() {
        continueShippingView.setVisibility(View.VISIBLE);
        continueShippingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setDialog(String dialogMessage, String dialogTitle) {
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
            Log.w(TAG, "failed to show dialog");
        }

    }
}

