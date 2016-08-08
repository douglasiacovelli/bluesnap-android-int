package com.bluesnap.androidapi.services;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

/**
 * A Collection of static methods used in the Android UI parts.
 */
public class AndroidUtil {
    public static final String NAME_FIELD = "NAME_FIELD";
    public static final String ZIP_FIELD = "ZIP_FIELD";
    public static final String EMAIL_FIELD = "EMAIL_FIELD";
    public static final String[] STATE_NEEDED_COUNTRIES = {"US", "BR", "CA"};
    protected static Calendar calendarInstance;
    protected static DecimalFormat decimalFormat;

    public static Calendar getCalendarInstance() {
        return calendarInstance != null ? (Calendar) calendarInstance.clone() : Calendar.getInstance();
    }


    public static boolean isBlank(String str) {
        return TextUtils.isEmpty(str);
    }

    public static boolean isYearInPast(int year) {
        Calendar now = getCalendarInstance();
        return year < now.get(Calendar.YEAR);
    }

    public static boolean isDateInFuture(int month, int year) {
        Calendar now = getCalendarInstance();
        if (year < 2000) {
            year += 2000;
        }
        return (year > now.get(Calendar.YEAR)) || (year == now.get(Calendar.YEAR) && month >= (now.get(Calendar.MONTH) + 1));
    }

    public static AndroidUtil getInstance() {
        return AndroidUtilHOLDER.INSTANCE;
    }

    public static void setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        //Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, null);
    }

    public static String getCurrencySymbol(String newCurrencyNameCode) {
        try {
            String symbol = Currency.getInstance(newCurrencyNameCode).getSymbol();
            return symbol;
        } catch (Exception e) {
            return newCurrencyNameCode;
        }

    }

    public static void setVisabilityRecursive(ViewGroup vg, int visability) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setVisibility(visability);
            if (child instanceof ViewGroup) {
                setVisabilityRecursive((ViewGroup) child, visability);
            }
        }
    }

    public static DecimalFormat getDecimalFormat() {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("#############.##");
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMinimumIntegerDigits(1);
        }
        return decimalFormat;
    }

    public static String stringify(Object s) {
        if (s == null || s.toString().isEmpty())
            return "";

        return s.toString().trim().replaceAll("\\s+", " ");
    }

    public static boolean checkCountryForState(String countryText) {
        for (String item : STATE_NEEDED_COUNTRIES) {
            if (item.equals(countryText)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validateEditTextString(EditText editText, TextView textView) {
        return validateEditTextString(editText, textView, null, null);
    }

    public static boolean validateEditTextString(EditText editText, TextView textView, String differentValidation) {
        return validateEditTextString(editText, textView, null, differentValidation);
    }

    public static boolean validateEditTextString(EditText editText, TextView textView, TextView optionalInvalidStatement) {
        return validateEditTextString(editText, textView, optionalInvalidStatement, null);
    }

    public static boolean validateEditTextString(EditText editText, TextView textView, TextView optionalInvalidStatement, String validationType) {
        String regex = "^[a-zA-Z0-9-]*$";
        String targetNoSpaces = editText.getText().toString().trim().replaceAll(" ", "");
        String target = editText.getText().toString();
        String[] splittedNames = target.trim().split(" ");

        if (TextUtils.isEmpty(targetNoSpaces) || targetNoSpaces.length() < 2) {
            if (optionalInvalidStatement != null)
                textInValidChanges(textView, optionalInvalidStatement);
            else
                textInValidChanges(textView);
            return false;
        } else if (validationType != null) {
            if ((NAME_FIELD.equals(validationType)) && (splittedNames.length < 2 || splittedNames[0].length() < 2)) {
                if (optionalInvalidStatement != null)
                    textInValidChanges(textView, optionalInvalidStatement);
                else
                    textInValidChanges(textView);
                return false;
            } else if ((ZIP_FIELD.equals(validationType)) && (!target.matches(regex))) {
                if (optionalInvalidStatement != null)
                    textInValidChanges(textView, optionalInvalidStatement);
                else
                    textInValidChanges(textView);
                return false;
            } else if ((EMAIL_FIELD.equals(validationType)) && (!Patterns.EMAIL_ADDRESS.matcher(target).matches())) {
                if (optionalInvalidStatement != null)
                    textInValidChanges(textView, optionalInvalidStatement);
                else
                    textInValidChanges(textView);
                return false;
            } else {
                if (optionalInvalidStatement != null)
                    textValidChanges(textView, optionalInvalidStatement);
                else
                    textValidChanges(textView);
                return true;
            }
        } else {
            if (optionalInvalidStatement != null)
                textValidChanges(textView, optionalInvalidStatement);
            else
                textValidChanges(textView);
            return true;
        }
    }

    private static void textValidChanges(TextView textView) {
        textView.setTextColor(Color.BLACK);
    }

    private static void textValidChanges(TextView textView, TextView optionalInvalidStatement) {
        textView.setTextColor(Color.BLACK);
        optionalInvalidStatement.setVisibility(View.INVISIBLE);
    }

    private static void textInValidChanges(TextView textView) {
        textView.setTextColor(Color.RED);
    }

    private static void textInValidChanges(TextView textView, TextView optionalInvalidStatement) {
        textView.setTextColor(Color.RED);
        optionalInvalidStatement.setVisibility(View.VISIBLE);
    }

    private static class AndroidUtilHOLDER {
        public static final AndroidUtil INSTANCE = new AndroidUtil();
    }
}
