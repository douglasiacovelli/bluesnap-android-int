package com.bluesnap.androidapi;

/**
 * Created by roy.biber on 08/06/2016.
 */
public class Constants {
   public static final String RETURNING_SHOPPER = "shopperinfo";
   public static final String REMEMBER_SHOPPER = "REMEMBER_SHOOPER";
   /* PayPal Process URL's */
   protected final static String PAYPAL_PROCEED_URL = "https://sandbox.bluesnap.com/jsp/dev_scripts/iframeCheck/pay_pal_proceed.html";
   protected final static String PAYPAL_CANCEL_URL = "https://sandbox.bluesnap.com/jsp/dev_scripts/iframeCheck/pay_pal_cancel.html";
   public static String SHIPPING_INFO = "SHIPPPING_INFO";

   public static String getPaypalProceedUrl() {
      return PAYPAL_PROCEED_URL;
   }
   public static String getPaypalCancelUrl() {
      return PAYPAL_CANCEL_URL;
   }

}
