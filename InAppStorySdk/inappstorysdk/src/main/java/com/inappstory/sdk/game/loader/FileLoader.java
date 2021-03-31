package com.inappstory.sdk.game.loader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileLoader {

    private static final ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService downExecutor = Executors.newFixedThreadPool(1);


    public static void unzipAsset(final Context context, final String fileName, final GameLoadCallback callback) {
        downExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    File directory = new File(context.getFilesDir() + "/zip/" + fileName.hashCode());
                    if (directory.exists())
                        callback.onLoad(directory);
                    else {
                        Log.e("GameDownloader", "file exists");
                        FileUnzipper.unzipAsset(context, fileName, directory);
                        callback.onLoad(directory);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError();
                }
            }
        });
    }

    public static void downloadAndUnzip(final Context context, final String url, final GameLoadCallback callback) {

        final Future<File> ff = runnableExecutor.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                int count;
                URL uri = new URL(url);
                File file = new File(context.getFilesDir() + "/zip/" + url.hashCode() + ".zip");
                if (file.exists()) return file;
                try {
                    file.mkdirs();
                } catch (Exception e) {

                }
                if (file.exists()) file.delete();
                URLConnection connection = uri.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(uri.openStream(),
                        8192);
                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return file;
            }
        });
        downExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = ff.get();
                    File directory = new File(file.getParent() + "/" + url.hashCode());
                    if (directory.exists())
                        callback.onLoad(directory);
                    else if (file.exists()) {
                        Log.e("GameDownloader", "file exists");
                        FileUnzipper.unzip(file, directory);
                        callback.onLoad(directory);
                    } else {
                        callback.onError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError();
                }
            }
        });
    }
}
