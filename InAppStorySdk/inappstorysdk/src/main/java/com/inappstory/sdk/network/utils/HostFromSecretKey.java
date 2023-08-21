package com.inappstory.sdk.network.utils;

import android.util.Base64;

import java.util.Arrays;

public class HostFromSecretKey {
    private String key;


    private static final String TEST_DOMAIN = "https://api.test.inappstory.com/";
    private static final String PRODUCT_DOMAIN = "https://api.inappstory.ru/";

    public HostFromSecretKey(String key) {
        this.key = key != null ? key : "";
    }

    public String get(boolean sandbox) {
        String domain = getFromKey();
        return domain != null ? domain : (sandbox ? TEST_DOMAIN : PRODUCT_DOMAIN);
    }
    private String getFromKey() {
        try {
            if (key == null || key.length() <= 32)
                return null;
            String domain = null;
            byte[] bytes = Base64.decode(key, 8);
            if (bytes == null || bytes.length < 14) return null;
            int count = bytes[13];
            if (count < 0) return null;
            StringBuilder keySt = new StringBuilder();
            while (keySt.length() <= count) {
                keySt.append("{QQN{xuV?1Dv16j3");
            }
            domain = xor(Arrays.copyOfRange(bytes, 14, 14 + count),
                    keySt.substring(0, count).getBytes());
            boolean matches = domain.startsWith("http://")
                    || domain.startsWith("https://");
            if (!matches) {
                domain = "https://" + domain;
            }
            if (!domain.endsWith("/")) domain += "/";
            return domain;
        } catch (Exception e) {
            return null;
        }
    }

    private String xor(byte[] str1, byte[] str2) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < str1.length; i++) {
            res.append((char) (str1[i] ^ str2[i]));
        }
        return res.toString();
    }
}
