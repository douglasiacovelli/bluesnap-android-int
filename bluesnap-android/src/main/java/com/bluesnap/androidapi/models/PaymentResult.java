package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Returns the result of the payment process to the Caller.
 * This will be passed as an activityResult back to the calling activity.
 */
public class PaymentResult implements Parcelable {


    public static final Creator<PaymentResult> CREATOR = new Creator<PaymentResult>() {
        @Override
        public PaymentResult createFromParcel(Parcel in) {
            return new PaymentResult(in);
        }

        @Override
        public PaymentResult[] newArray(int size) {
            return new PaymentResult[size];
        }
    };
    public String last4Digits;
    public Double amount;
    public String currencyNameCode;
    public String shopperID;
    public String cardType;
    public String expDate;
    public String shopperFirstName;
    public String shopperLastName;
    public String cardZipCode;
    public boolean rememberUser;
    public boolean returningTransaction;

    public PaymentResult() {
    }

    protected PaymentResult(Parcel in) {
        shopperID = in.readString();
        last4Digits = in.readString();
        amount = in.readDouble();
        currencyNameCode = in.readString();
        cardType = in.readString();
        expDate = in.readString();
        shopperFirstName = in.readString();
        shopperLastName = in.readString();
        cardZipCode = in.readString();
        rememberUser = in.readInt() != 0;
        returningTransaction = in.readInt() != 0;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shopperID);
        dest.writeString(last4Digits);
        dest.writeDouble(amount);
        dest.writeString(currencyNameCode);
        dest.writeString(cardType);
        dest.writeString(expDate);
        dest.writeString(shopperFirstName);
        dest.writeString(shopperLastName);
        dest.writeString(cardZipCode);
        dest.writeInt(rememberUser ? 1 : 0);
        dest.writeInt(returningTransaction ? 1 : 0);
    }

    public boolean isReturningTransaction() {
        return returningTransaction;
    }

    public void setReturningTransaction(boolean returningTransaction) {
        this.returningTransaction = returningTransaction;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentResult that = (PaymentResult) o;

        if (!last4Digits.equals(that.last4Digits)) return false;
        if (!amount.equals(that.amount)) return false;
        if (!currencyNameCode.equals(that.currencyNameCode)) return false;
        if (!shopperID.equals(that.shopperID)) return false;
        return cardType.equals(that.cardType);

    }

    @Override
    public int hashCode() {
        int result = last4Digits.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + currencyNameCode.hashCode();
        result = 31 * result + shopperID.hashCode();
        result = 31 * result + cardType.hashCode();
        return result;
    }

    public boolean validate() {
        if (amount == null || amount.equals(0.0)) return false;
        if (currencyNameCode == null || currencyNameCode.isEmpty()) return false;
        if (expDate == null || expDate.isEmpty()) return false;
        return !(last4Digits == null || Integer.valueOf(last4Digits) == 0);
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "last4Digits='" + last4Digits + '\'' +
                ", amount=" + amount +
                ", currencyNameCode='" + currencyNameCode + '\'' +
                ", shopperID='" + shopperID + '\'' +
                ", cardType='" + cardType + '\'' +
                ", expDate='" + expDate + '\'' +
                ", shopperFirstName='" + shopperFirstName + '\'' +
                ", shopperLastName='" + shopperLastName + '\'' +
                ", cardZipCode='" + cardZipCode + '\'' +
                ", rememberUser=" + rememberUser + '\'' +
                ", returningTransaction=" + returningTransaction + '\'' +
                '}';
    }
}
