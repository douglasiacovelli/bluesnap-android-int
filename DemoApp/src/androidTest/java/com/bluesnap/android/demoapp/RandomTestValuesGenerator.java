package com.bluesnap.android.demoapp;

import java.util.Random;

/**
 *
 */
public class RandomTestValuesGenerator {
    final double MINIMUM_AMOUNT = 0.01D;
    final double MINIMUM_TAX_PRECENT_AMOUNT = 0;
    double MAXIMUM_AMOUNT = Double.MAX_VALUE / 2;
    Random random = new Random();

    public Double randomDemoAppPrice() {
        double result = MINIMUM_AMOUNT + (random.nextDouble() * (MAXIMUM_AMOUNT - MINIMUM_AMOUNT));
        return result;
    }

    public Double randomTax() {
        double result = MINIMUM_AMOUNT + (random.nextInt() * (100 - MINIMUM_TAX_PRECENT_AMOUNT));
        return result;
    }

}
