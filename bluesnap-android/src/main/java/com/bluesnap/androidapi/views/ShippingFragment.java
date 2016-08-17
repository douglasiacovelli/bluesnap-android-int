package com.bluesnap.androidapi.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluesnap.androidapi.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.Constants;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.Events;
import com.bluesnap.androidapi.models.PaymentRequest;
import com.bluesnap.androidapi.models.ShippingInfo;
import com.bluesnap.androidapi.services.AndroidUtil;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.PrefsStorage;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by oz on 12/2/15.
 */
public class ShippingFragment extends Fragment implements BluesnapPaymentFragment {
    public static final String AUTO_POPULATE_SHOPPER_NAME = "AUTO_POPULATE_SHOPPER_NAME";
    public static final String AUTO_POPULATE_ZIP = "AUTO_POPULATE_ZIP";
    public static final String SHIPPING_TAG = String.valueOf(ShippingFragment.class.getSimpleName());
    static final String TAG = ShippingFragment.class.getSimpleName();
    private TextView totalAmountTextView;
    private ShippingInfo shippingInfo;
    private EditText shippingNameEditText;
    private TextView invalidNameMessageTextView;
    private TextView shippingNameLabelTextView;
    private EditText shippingEmailEditText;
    private TextView invalidEmailMessageTextView;
    private TextView shippingEmailLabelTextView;
    private EditText shippingAddressLineEditText;
    private TextView invalidAddressMessageTextView;
    private TextView shippingAdressLabelTextView;
    private EditText shippingCityEditText;
    private TextView shippingCityLabelTextView;
    private EditText shippingStateEditText;
    private TextView shippingStateLabelTextView;
    private EditText shippingZipEditText;
    private TextView shippingZipLabelTextView;
    private Button addressCountryButton;
    private PrefsStorage prefsStorage;
    private boolean validInput;
    private ViewGroup subtotalView;
    private TextView subtotalValueTextView;
    private TextView taxValueTextView;
    private LinearLayout shippingFieldsLinearLayout;

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) {
                return simCountry.toUpperCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) {
                    return networkCountry.toUpperCase(Locale.US);
                }
            }
        } catch (Exception e) {
            Log.e(SHIPPING_TAG, "TelephonyManager, getSimCountryIso or getNetworkCountryIso failed");
        }

        return Locale.US.getCountry();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.bluesnap_shipping, container, false);
        shippingNameEditText = (EditText) inflate.findViewById(R.id.shippingNameEditText);
        shippingNameLabelTextView = (TextView) inflate.findViewById(R.id.shippingNameLabelTextView);
        invalidNameMessageTextView = (TextView) inflate.findViewById(R.id.invalidNameMessageTextView);
        shippingEmailEditText = (EditText) inflate.findViewById(R.id.shippingEmailEditText);
        shippingEmailLabelTextView = (TextView) inflate.findViewById(R.id.shippingEmailLabelTextView);
        invalidEmailMessageTextView = (TextView) inflate.findViewById(R.id.invalidEmailMessageTextView);
        shippingAddressLineEditText = (EditText) inflate.findViewById(R.id.shippingAddressLine);
        shippingAdressLabelTextView = (TextView) inflate.findViewById(R.id.addressLineLabelTextView);
        invalidAddressMessageTextView = (TextView) inflate.findViewById(R.id.invaildAddressMessageTextView);
        shippingCityEditText = (EditText) inflate.findViewById(R.id.shippingCityEditText);
        shippingCityLabelTextView = (TextView) inflate.findViewById(R.id.addressCityView);
        shippingStateEditText = (EditText) inflate.findViewById(R.id.shippingStateEditText);
        shippingStateLabelTextView = (TextView) inflate.findViewById(R.id.shippingStateLabelTextView);
        shippingZipEditText = (EditText) inflate.findViewById(R.id.shippingZipEditText);
        shippingZipLabelTextView = (TextView) inflate.findViewById(R.id.addressZipView);
        addressCountryButton = (Button) inflate.findViewById(R.id.addressCountryEditText);
        totalAmountTextView = (TextView) inflate.findViewById(R.id.shippingBuyNowButton);
        prefsStorage = new PrefsStorage(inflate.getContext());
        subtotalView = (ViewGroup) inflate.findViewById(R.id.subtotal_tax_table_shipping);
        subtotalValueTextView = (TextView) inflate.findViewById(R.id.subtotalValueTextviewShipping);
        taxValueTextView = (TextView) inflate.findViewById(R.id.taxValueTextviewShipping);
        shippingFieldsLinearLayout = (LinearLayout) inflate.findViewById(R.id.shippingFieldsLinearLayout);
        AndroidUtil.hideKeyboardOnLayoutPress(shippingFieldsLinearLayout);

        addressCountryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(inflate.getContext(), CountryActivity.class);
                newIntent.putExtra(getString(R.string.COUNTRY_STRING), getCountryText());
                startActivityForResult(newIntent, Activity.RESULT_FIRST_USER);
            }
        });
        return inflate;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_FIRST_USER) {
            if (resultCode == Activity.RESULT_OK) {
                addressCountryButton.setText(data.getStringExtra("result"));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (AndroidUtil.checkCountryForState(getCountryText())) {
            ActivateOnFocusValidation(shippingStateEditText);
        } else {
            shippingStateEditText.setOnFocusChangeListener(null);
        }
    }

    @Subscribe
    public void onCurrencyUpdated(Events.CurrencyUpdatedEvent currencyUpdatedEvent) {
        String currencySymbol = AndroidUtil.getCurrencySymbol(currencyUpdatedEvent.newCurrencyNameCode);
        DecimalFormat decimalFormat = AndroidUtil.getDecimalFormat();
        totalAmountTextView.setText(getResources().getString(R.string.pay) + " " + currencySymbol + " " + decimalFormat.format(currencyUpdatedEvent.updatedPrice));

        String taxValue = currencySymbol + String.valueOf(decimalFormat.format(currencyUpdatedEvent.updatedTax));
        taxValueTextView.setText(taxValue);
        String subtotal = currencySymbol + decimalFormat.format(currencyUpdatedEvent.updatedSubtotal);
        subtotalValueTextView.setText(subtotal);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final PaymentRequest paymentRequest = BlueSnapService.getInstance().getPaymentRequest();
        Events.CurrencyUpdatedEvent currencyUpdatedEvent = new Events.CurrencyUpdatedEvent(paymentRequest.getAmount(), paymentRequest.getCurrencyNameCode(), paymentRequest.getTaxAmount(), paymentRequest.getSubtotalAmount());
        onCurrencyUpdated(currencyUpdatedEvent);

        boolean notax = (paymentRequest.getSubtotalAmount() == 0D || paymentRequest.getTaxAmount() == 0D);
        subtotalView.setVisibility(notax ? View.INVISIBLE : View.VISIBLE);

        totalAmountTextView.setOnClickListener(new ShippingSubmitClickListener());

        ShippingInfo shippingInfo = (ShippingInfo) prefsStorage.getObject(Constants.SHIPPING_INFO, ShippingInfo.class);
        //Card shopper = (Card) prefsStorage.getObject(Constants.RETURNING_SHOPPER, Card.class);
        if (shippingInfo != null) {
            shippingNameEditText.setText(shippingInfo.getName());
            shippingAddressLineEditText.setText(shippingInfo.getAddressLine());
            shippingCityEditText.setText(shippingInfo.getShippingCity());
            shippingStateEditText.setText(shippingInfo.getState());
            shippingZipEditText.setText(shippingInfo.getZip());
            addressCountryButton.setText(shippingInfo.getCountry());
            shippingEmailEditText.setText(shippingInfo.getEmail());
        } else {
            savedInstanceState = getArguments();
            shippingNameEditText.setText(savedInstanceState.getString(AUTO_POPULATE_SHOPPER_NAME));
            shippingZipEditText.setText(savedInstanceState.getString(AUTO_POPULATE_ZIP));
            addressCountryButton.setText(getUserCountry(getActivity().getApplicationContext()));
        }

        validInput = false;
        ActivateOnFocusValidation(shippingNameEditText);
        ActivateOnFocusValidation(shippingAddressLineEditText);
        ActivateOnFocusValidation(shippingCityEditText);
        ActivateOnEditorActionListener(shippingCityEditText);
        ActivateOnFocusValidation(shippingZipEditText);
        ActivateOnFocusValidation(shippingEmailEditText);
        if (AndroidUtil.checkCountryForState(getCountryText())) {
            ActivateOnFocusValidation(shippingStateEditText);
            ActivateOnEditorActionListener(shippingStateEditText);
        } else {
            shippingStateEditText.setOnFocusChangeListener(null);
        }

        AndroidUtil.setFocusOnLayoutOfEditText(shippingNameLabelTextView, shippingNameEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingEmailLabelTextView, shippingEmailEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingAdressLabelTextView, shippingAddressLineEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingZipLabelTextView, shippingZipEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingCityLabelTextView, shippingCityEditText);
        AndroidUtil.setFocusOnLayoutOfEditText(shippingStateLabelTextView, shippingStateEditText);
    }

    private boolean Validation(EditText editText) {
        if (editText == shippingAddressLineEditText) {
            return AndroidUtil.validateEditTextString(shippingAddressLineEditText, shippingAdressLabelTextView, invalidAddressMessageTextView);
        } else if (editText == shippingCityEditText) {
            return AndroidUtil.validateEditTextString(shippingCityEditText, shippingCityLabelTextView);
        } else if (editText == shippingStateEditText) {
            return AndroidUtil.validateEditTextString(shippingStateEditText, shippingStateLabelTextView);
        } else if (editText == shippingZipEditText) {
            return AndroidUtil.validateEditTextString(shippingZipEditText, shippingZipLabelTextView, AndroidUtil.ZIP_FIELD);
        } else if (editText == shippingNameEditText) {
            return AndroidUtil.validateEditTextString(shippingNameEditText, shippingNameLabelTextView, invalidNameMessageTextView, AndroidUtil.NAME_FIELD);
        } else if (editText == shippingEmailEditText) {
            return AndroidUtil.validateEditTextString(shippingEmailEditText, shippingEmailLabelTextView, invalidEmailMessageTextView, AndroidUtil.EMAIL_FIELD);
        }
        return false;
    }

    private void ActivateOnFocusValidation(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validInput = Validation(editText);
                }
            }
        });
    }

    private void ActivateOnEditorActionListener(final EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    validInput = Validation(editText);
                return false;
            }
        });
    }

    private boolean checkStateValidation() {
        if (AndroidUtil.checkCountryForState(getCountryText())) {
            return Validation(shippingStateEditText);
        } else {
            shippingStateLabelTextView.setTextColor(Color.BLACK);
            return true;
        }
    }

    private String getCountryText() {
        return AndroidUtil.stringify(addressCountryButton.getText()).trim();
    }

    private class ShippingSubmitClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            validInput = Validation(shippingNameEditText);
            validInput &= Validation(shippingEmailEditText);
            validInput &= Validation(shippingAddressLineEditText);
            validInput &= Validation(shippingZipEditText);
            validInput &= Validation(shippingCityEditText);
            validInput &= checkStateValidation();

            if (validInput) {
                ShippingInfo shippingInfo = new ShippingInfo();
                shippingInfo.setName(shippingNameEditText.getText().toString().trim());
                shippingInfo.setAddressLine(shippingAddressLineEditText.getText().toString().trim());
                shippingInfo.setShippingCity(shippingCityEditText.getText().toString().trim());
                shippingInfo.setState(shippingStateEditText.getText().toString().trim());
                shippingInfo.setCountry(getCountryText());
                shippingInfo.setZip(shippingZipEditText.getText().toString().trim());
                shippingInfo.setEmail(shippingEmailEditText.getText().toString().trim());
                BluesnapCheckoutActivity bluesnapCheckoutActivity = (BluesnapCheckoutActivity) getActivity();
                bluesnapCheckoutActivity.finishFromShippingFragment(shippingInfo);
            }
        }
    }
}
