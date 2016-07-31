package com.bluesnap.androidapi.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Collection of shipping details used in {@link com.bluesnap.androidapi.views.ShippingFragment}
 */
public class ShippingInfo implements Parcelable {
    public static final Creator<ShippingInfo> CREATOR = new Creator<ShippingInfo>() {
        @Override
        public ShippingInfo createFromParcel(Parcel in) {
            return new ShippingInfo(in);
        }

        @Override
        public ShippingInfo[] newArray(int size) {
            return new ShippingInfo[size];
        }
    };
    private String name;
    private String addressLine;
    private String shippingCity;
    private String state;
    private String zip;
    private String country;
    private String email;



    protected ShippingInfo(Parcel parcel) {
        name = parcel.readString();
        addressLine = parcel.readString();
        shippingCity = parcel.readString();
        state = parcel.readString();
        zip = parcel.readString();
        country = parcel.readString();
        email = parcel.readString();
    }

    public ShippingInfo() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(addressLine);
        parcel.writeString(shippingCity);
        parcel.writeString(state);
        parcel.writeString(zip);
        parcel.writeString(country);
        parcel.writeString(email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShippingInfo that = (ShippingInfo) o;

        if (!name.equals(that.name)) return false;
        if (!addressLine.equals(that.addressLine)) return false;
        if (!shippingCity.equals(that.shippingCity)) return false;
        if (zip != null ? !zip.equals(that.zip) : that.zip != null) return false;
        if (!country.equals(that.country)) return false;
        return email != null ? email.equals(that.email) : that.email == null;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + addressLine.hashCode();
        result = 31 * result + shippingCity.hashCode();
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + country.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShippingInfo{" +
                "name='" + name + '\'' +
                ", addressLine='" + addressLine + '\'' +
                ", shippingCity='" + shippingCity + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
