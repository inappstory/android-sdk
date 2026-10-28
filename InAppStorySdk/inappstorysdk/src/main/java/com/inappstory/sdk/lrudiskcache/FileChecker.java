package com.inappstory.sdk.lrudiskcache;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Objects;

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

    public FileCheckerResult checkWithShaAndSize(File file, Long size, String sha, boolean removeIfNotCorrect) {
        if (file == null || !file.exists()) return new FileCheckerError("file not exist");
        if (size == null || size <= 0 || sha == null || sha.isEmpty())
            return new FileCheckerSuccess();
        FileCheckerResult result = new FileCheckerSuccess();
        if (file.length() != size) {
            result = new FileCheckerError("expected size: " + size + ", actual: " + file.length());
        } else {
            String actualSha = getFileSHA1(file);
            if (!Objects.equals(actualSha, sha)) {
                result = new FileCheckerError("expected sha1: " + sha + ", actual: " + actualSha);
            }
        }
        if (result instanceof FileCheckerError && removeIfNotCorrect) {
            file.delete();
        }
        return result;
    }
}
