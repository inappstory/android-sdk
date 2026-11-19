package com.inappstory.sdk.lrudiskcache;

import com.inappstory.sdk.stories.cache.DownloadFileState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileManager {

    public FileManager(File cacheDir, String subPath) throws IOException {
        this.cacheDir = cacheDir;
        prepareCacheDir(subPath);
    }

    public File getCacheDir() {
        return cacheDir;
    }

    private File cacheDir;

    public File getJournalFile() {
        File file = new File(cacheDir, CacheJournal.JOURNAL_NAME);
        try {
            cacheDir.mkdirs();
            if (!file.exists()) file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public void delete(String name) throws IOException {
        File file = new File(cacheDir, name);
        if (!file.exists()) {
            file = new File(name);
        }
        if (file.exists())
            deleteRecursive(file);
    }

    public static String getStringFromFile(File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static boolean deleteRecursive(File fileOrDirectory) {
        boolean res = true;
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                res &= deleteRecursive(child);
            }
        }

        res &= fileOrDirectory.delete();
        return res;
    }

    public static void deleteFolderRecursive(File fileOrDirectory,
                                             boolean deleteRoot,
                                             String excludePattern
    ) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteFolderRecursive(child, true, excludePattern);
            }
        }
        if (deleteRoot) {
            try {
                if (excludePattern != null &&
                        fileOrDirectory.getAbsolutePath().contains(excludePattern)) return;
                fileOrDirectory.delete();
            } catch (Exception e) {

            }
        }
    }

    public File get(String name) {
        return new File(cacheDir, name);
    }

    public boolean exists(String name) {
        return new File(cacheDir, name).exists();
    }

    private boolean checkAndUse(File baseDir, String subPath) throws IOException {
        if (baseDir == null) return false;
        File file = new File(baseDir + subPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                if (!baseDir.canWrite()) {
                    return false;
                } else {
                    File file1 = new File(baseDir + File.separator + "testFile");
                    if (file1.createNewFile()) {
                        file1.delete();
                        cacheDir = baseDir;
                        return true;
                    } else {
                        return false;
                    }
                }

            }
        }
        cacheDir = new File(baseDir + subPath);
        return true;
    }

    public void prepareCacheDir(String subPath) throws IOException {
        if (!checkAndUse(cacheDir, subPath)) {
            throw formatException("Unable to use cache directory %s", cacheDir);
        }
    }


    public static File getFullFile(DownloadFileState fileState) {
        if (fileState != null)
            return fileState.getFullFile();
        return null;
    }

    public File put(File extFile, String name) throws IOException {
        File newFile = new File(name);
        if ((cacheDir.exists() || cacheDir.mkdirs())
                | (newFile.exists())
                | extFile.renameTo(newFile)) {
            return newFile;
        } else {
            throw formatException("Unable to use file %s", extFile);
        }
    }

    private IOException formatException(String format, File file) {
        String message = String.format(format, file.getName());
        return new IOException(message);
    }
}
