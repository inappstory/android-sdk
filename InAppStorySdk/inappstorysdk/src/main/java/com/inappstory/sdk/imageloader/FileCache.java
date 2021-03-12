package com.inappstory.sdk.imageloader;

import android.content.Context;

import java.io.File;

public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {
        //Find the dir to save cached images

        cacheDir = context.getCacheDir();
        if (cacheDir!= null && !cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        if (cacheDir == null) return null;
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}