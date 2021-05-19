package com.inappstory.sdk.stories.cache.lrudiskcache;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileManager {
    public FileManager(File cacheDir) throws IOException {
        this.cacheDir = cacheDir;
        prepare();
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

    public File getCacheDir() {
        return cacheDir;
    }

    private File cacheDir;

    public File getJournalFile() {
        return new File(cacheDir, "journal.bin");
    }

    public void delete(String name) throws IOException {
        File file = new File(cacheDir, name);
        if (file.exists() && !deleteRecursive(file)) {
            throw formatException("Unable to delete file %s", file);
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

    public void prepare() throws IOException {
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                throw formatException("Unable to use cache directory %s", cacheDir);
            }
        }
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
