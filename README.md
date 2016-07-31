## bluesnap-android

## Android Studio (Gradle) instructions
To get started, add the following line in your 'build.gradle' dependencies section:

    compile 'com.bluesnap:bluensap-sdk:+'

### Usage

#### Generate a token on your server side and pass it to the SDK
A Unique merchant token is required for all SDK operations. you can obtain merchant tokens by initiation a server to server call with your credentials.
For each transaction, a new merchant token must be provided.
When you obtain a merchant token from our sandbox servers and pass it to the SDK, The SDK will be configured to work agains sandbox URLs. This is done automatically based on the token structure.


#### Initialize the BlueSnap Service with the Token
In order to initialize the sdk with the token, you need to pass the token to the setup method of the BluensapService class.

    BlueSnapService.getInstance().setup("MERCHANT_TOKEN_STRING");

The setup() method will initiate the SDK, prepare it for rate conversion and initialize the required objects for the user interface. No UI interaction will happen at this stage.
Since the Token is valid for 12 Hours, only initialize the SDK with the token close to the purchase time. if you only want to use the SDK rate conversion service prior to the purchase, you should use another token.


#### Create a PaymentRequest.
A PaymentRequest is required to pass information about the purchase to the SDK. The request must include the purchase amount and currency, as an [ISO 4217](http://www.iso.org/iso/home/standards/currency_codes.htm) currency name

    PaymentRequest paymentRequest = new PaymentRequest();        
    paymentRequest.setAmount("20.5"D);
    paymentRequest.setCurrencyNameCode("USD");

Optionally, you may pass a tax amount and a subtotal price.

    setAmountWithTax(Double subtotalAmount, Double taxAmount);


The payment request allows you to pass additional parameters to the SDK such as title or a custom text to be displayed to the merchant.

    paymentRequest.setCustomDescription();
        
 
A PaymentResult will be generated at the SDK and passed back to your application to complete the transaction.

#### Display BluesnapCheckoutActivity 
The SDK includes implementations of a checkout activity, that allows you to easily collect the required information for checkout without having to deal with Card number validation, expiry date and sensitive information collection.
To start the activity simply create an Android Intent, and pass the PaymentRequest as an Intent Extra.

        Intent intent = new Intent(getApplicationContext(), BluesnapCheckoutActivity.class);
        intent.putExtra(BluesnapCheckoutActivity.EXTRA_PAYMENT_REQUEST, paymentRequest);
        startActivityForResult(intent);
        
This will launch the activity and take care of the interaction with the user. At the end of the process, you'll get an Android Activity Result with a PaymentResult object.
As part of the checkout process, the shopper's card details will be transmitted securely to Bluesnap's Servers. you will not have to keep the card number in your application

 
 
#### The PaymentResult
A PaymentResult Encapsulates the required information needed for creating the transaction at your server side. Specifically, this includes the last 4 digits of the card, the Card type, and The shopper name in case of credit card checkout, and a transaction ID in case of an express checkout methos
The PaymentResult is passed back to your activity as an activityResult Extra. In order to get an activity result From BluesnapCheckoutActivity, you need to implement [onActivityResult](https://developer.android.com/training/basics/intents/result.html) ();
        
        
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (!data) // User aborted the checkout process
                return;
                
            PaymentResult paymentResult = (PaymentResult) data.getExtras().get(BluesnapCheckoutActivity.EXTRA_PAYMENT_RESULT);
        }


#### Shipping information
The SDK includes shipping information collection activity. To indicate that Shipping information needs to be collected, user the setShippingRequired method in the PaymentRequest.

       ShippingInfo shippingInfo = (ShippingInfo) extras.get(BluesnapCheckoutActivity.EXTRA_SHIPPING_DETAILS);
        

#### Completing the transaction
In order to complete the transaction, you will have to make a server-to-server call with the merchant token you used with the SDK. This should happen after the user has left the SDK checkout screen and finished the interaction with the SDK.


### Additional functionality
#### Currency conversion
The SDK Provides Currency rates conversion service, to provide the best user experience, the rates are fetched in a single http request and converted locally on the device.
You can use the rates conversion without having to initialize any user interface parts of the SDK, but you still need to initialize the SDK with a token.
The SDK provides the following methods for your convenience: 

    updateRates()
    getSupportedRates()
    convertUSD()
    convertPrice()


### Express checkout
The user can choose to pay with express checkout methods such as Paypal.
If a user will carry out a purchase with PayPal, a paypal transaction ID will be passed as part of the PaymentResult.
All the other fields which are relevant to a credit card transaction will be empty.


### Customization and UI Overrides
It is possible to customize the checkout experience, change colors, icons and basic layouts.
One way to achieve that is by overriding the SDK resources files in your application and provide matching resource file names to override the SDK default values.



### Translation
The SDK includes translated resourced for a large number of languages, the Android framework will automatically pick up the translation according to the Android framework locale.
 

## Demo application.

#### Prerequisites
BlueSnap Android SDK supports Android SDK 23 and above for development. The minimum Android API version for applications is 15, which covers more than 98% of the [Android devices out there](https://developer.android.com/about/dashboards/index.html). 
 
1. To get started with the demo application clone the git repository.
2. Import the project by choosing "Import Project" and selecting the build.gradle file in the checkout directory.
3. Build and run the DemoApp on your device.

#### Demo app token
The Demo app will obtain a merchant token from BlueSnap sandbox servers using HTTP calls and demo credentials. This procedure should be replaced by your server side calls.


## ProGuard exclude
If you're running ProGuard as part of your build process make sure to exclude the Gson. to do this please add [this](https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg) to your proguard.cfg file.

