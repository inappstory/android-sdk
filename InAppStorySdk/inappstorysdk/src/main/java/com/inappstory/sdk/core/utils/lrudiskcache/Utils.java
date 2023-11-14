package com.inappstory.sdk.core.utils.lrudiskcache;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;

public class Utils {
    private static String HASH_ALGORITHM = "SHA512";


    public static String hash(@NonNull String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString() + "_u0";

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static class TimeComparator implements Comparator<CacheJournalItem> {

        @Override
        public int compare(CacheJournalItem item1, CacheJournalItem item2) {
            return compare(item2.getTime(), item1.getTime());
        }

        @SuppressWarnings("UseCompareMethod")
        @VisibleForTesting
        public static int compare(long x, long y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }

    }
}
