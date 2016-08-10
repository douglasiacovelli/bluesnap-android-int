package com.bluesnap.android.demoapp;

import android.support.test.espresso.action.ViewActions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by oz on 5/30/16.
 */
public class CardFormTesterCommon {

    public static void fillInAllFieldsWithValidCard() {
        onView(withId(R.id.creditCardNumberEditText))
                .perform(typeText(cardNumberGeneratorTest()), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.emailEditText)).perform(typeText("John Doe"));
        onView(withId(R.id.expDateEditText)).perform(typeText("11"));
        onView(withId(R.id.expDateEditText)).perform(typeText("2011"));
        onView(withId(R.id.cvvEditText)).perform(typeText("123")).perform(ViewActions.closeSoftKeyboard());

    }

    public static void fillInShippingDetails() {
        onView(withId(R.id.shippingNameEditText)).perform(typeText("John Doe"));
        onView(withId(R.id.shippingAddressLine)).perform(typeText("9 Baker street"));
        onView(withId(R.id.shippingCityEditText)).perform(typeText("London"));
        onView(withId(R.id.shippingStateEditText)).perform(typeText("UK")).perform(ViewActions.closeSoftKeyboard());

    }

    public static String cardNumberGeneratorTest() {
        return "5572758886015288";
    }

    public static String invalidCardNumberGeneratorTest() {
        return "557275888112233";
    }


}
