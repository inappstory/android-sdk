package com.inappstory.sdk.packages.core.base.files;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class FileChecker {

    public String getFileSHA1(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            FileInputStream fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];

            int nread = 0;

            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            byte[] mdbytes = md.digest();

            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public boolean checkWithShaAndSize(File file, Long size, String sha, boolean removeIfNotCorrect) {
        if (file == null || !file.exists()) return false;
        if (size == null || size <= 0 || sha == null || sha.isEmpty()) return true;
        boolean compare = file.length() == size && getFileSHA1(file).equals(sha);
        if (!compare && removeIfNotCorrect) {
            file.delete();
        }
        return compare;
    }
}
