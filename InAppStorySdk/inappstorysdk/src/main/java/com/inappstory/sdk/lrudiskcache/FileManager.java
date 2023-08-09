package com.inappstory.sdk.lrudiskcache;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.cache.DownloadFileState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileManager {
    public FileManager(File cacheDir, String subPath) throws IOException {
        this.cacheDir = cacheDir;
        prepare(subPath);
    }

    public long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }

    public static ArrayList<String> recursiveFileList(File directory) {
        ArrayList<String> res = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                res.addAll(recursiveFileList(file));
            } else {
                res.add(file.getAbsolutePath());
            }
        }
        return res;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    private File cacheDir;

    public File getJournalFile() {
        File file = new File(cacheDir, "journal.bin");
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
        if (!file.exists() || !deleteRecursive(file)) {
            return;
//            throw formatException("Unable to delete file %s", file);
        }
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

    public File get(String name) {
        return new File(cacheDir, name);
    }

    public boolean exists(String name) {
        return new File(cacheDir, name).exists();
    }

    public boolean checkAndUse(File baseDir, String subPath) throws IOException {
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

    public void prepare(String subPath) throws IOException {
        if (!checkAndUse(cacheDir, subPath)) {
            if (!checkAndUse(InAppStoryManager.getInstance().getContext().getFilesDir(),
                    subPath)) {
                throw formatException("Unable to use cache directory %s", cacheDir);
            }
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

    public static String getFileSHA1(File file) {
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

    public static boolean checkShaAndSize(File file, Long size, String sha) {
        if (size == null || size <= 0 || sha == null || sha.isEmpty()) return true;
        return file.length() == size && getFileSHA1(file).equals(sha);
    }
}
