package com.bluesnap.androidapi.services;

/**
 * Created by roy.biber on 04/08/2016.
 */
public class BluesnapToken {
    private static final String QA3_URL = "https://us-qa-fct03.bluesnap.com/services/2/";
    private static final String SANDBOX_URL = "https://sandbox.bluesnap.com/services/2/";
    private static final String PROD_PART_1_URL = "https://www";
    private static final String PROD_PART_2_URL = ".bluesnap.com/services/2/";
    private String url;
    private String merchantToken;


    public BluesnapToken(String merchantToken) {
        this.merchantToken = merchantToken;
    }

    public String getUrl() throws IllegalStateException {
        if (AndroidUtil.isBlank(url))
            throw new IllegalStateException("token no set must call set token");
        return url;
    }

    public void setToken(String token) throws IllegalArgumentException {
        if (AndroidUtil.isBlank(token) && token.length() > 2)
            throw new IllegalArgumentException("malformed token");

        String lastChar = token.substring(token.length() - 1);
        if ("_".equals(lastChar))
            url = QA3_URL; // SANDBOX_URL;
        else if ("1".equals(lastChar) || "2".equals(lastChar))
            url = PROD_PART_1_URL + lastChar + PROD_PART_2_URL;
        else
            throw new IllegalArgumentException("ilegal token");
    }

    public String getMerchantToken() {
        return merchantToken;
    }
}
