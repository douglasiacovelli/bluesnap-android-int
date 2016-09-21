package com.bluesnap.androidapi.models;

/**
 * A representation of server exchange rate.
 */
public class ExchangeRate {
    private String quoteCurrency;
    private double conversionRate;
    private double inverseConversionRate;

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
        if (conversionRate != 0)
            setInverseConversionRate(1 / conversionRate);
        else
            setInverseConversionRate(0);
    }

    public double getInverseConversionRate() {
        return inverseConversionRate;
    }

    private void setInverseConversionRate(double inverseConversionRate) {
        this.inverseConversionRate = inverseConversionRate;
    }
}
