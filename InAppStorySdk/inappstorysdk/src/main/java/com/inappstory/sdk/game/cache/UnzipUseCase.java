package com.inappstory.sdk.game.cache;

import androidx.annotation.WorkerThread;

import com.inappstory.sdk.utils.ProgressCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUseCase {
    String zipFilePath;

    public UnzipUseCase(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    @WorkerThread
    public boolean unzip(String targetDirectoryPath, ProgressCallback callback) {
        try {
            File zipFile = new File(zipFilePath);
            if (!zipFile.exists()) return false;
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(new FileInputStream(zipFile)));
            try {
                File targetDirectory = new File(targetDirectoryPath);
                if (!targetDirectory.isDirectory() && !targetDirectory.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            targetDirectory.getAbsolutePath());

                ZipEntry ze;
                int count;
                byte[] buffer = new byte[8192];
                long totalLength = zipFile.length();
                long curLength = 0;
                while ((ze = zis.getNextEntry()) != null) {
                    File file = new File(targetDirectoryPath, ze.getName());
                    File dir = ze.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (ze.isDirectory())
                        continue;
                    String canonicalPath = file.getCanonicalPath();
                    if (!canonicalPath.startsWith(targetDirectory.getCanonicalPath())) {
                        continue;
                    }
                    FileOutputStream fout = new FileOutputStream(file);
                    try {
                        while ((count = zis.read(buffer)) != -1)
                            fout.write(buffer, 0, count);
                    } finally {
                        fout.close();
                    }
                    if (callback != null) {
                        curLength += ze.getCompressedSize();
                        callback.onProgress(curLength, totalLength);
                    }
                }
                return true;
            } finally {
                zis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
