package com.inappstory.sdk.game.loader;

import android.content.Context;
import android.util.Log;

import com.inappstory.sdk.stories.api.models.WebResource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Callable;
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

    public static void deleteFolderRecursive(File fileOrDirectory, boolean deleteRoot) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteFolderRecursive(child, true);
            }
        }
        if (deleteRoot) {
            try {
                fileOrDirectory.delete();
            } catch (Exception e) {

            }
        }
    }

    public static void downloadResources(final List<WebResource> resources,
                                        final String pathName,
                                        final GameLoadCallback callback) {
        final File filePath = new File(pathName + "/src/");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        final Future<Void> ff = runnableExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                int cnt = 0;
                int sz = 0;
                for (WebResource resource : resources) {
                    sz += resource.size;
                }
                for (WebResource resource : resources) {
                    String url = resource.url;
                    String fileName = resource.key;
                    if (url == null || url.isEmpty() || fileName == null || fileName.isEmpty()) continue;
                    int count;
                    URL uri = new URL(url);
                    File file = new File(filePath.getAbsolutePath() + "/" + fileName);
                    if (file.exists())
                        continue;
                    URLConnection connection = uri.openConnection();
                    connection.connect();
                    InputStream input = new BufferedInputStream(uri.openStream(),
                            8192);
                    OutputStream output = new FileOutputStream(file);
                    byte data[] = new byte[1024];
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                        cnt += count;
                        callback.onProgress(cnt, sz);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                return null;
            }
        });
        downExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ff.get();
                    callback.onLoad(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError();
                }
            }
        });
    }

    public static void downloadAndUnzip(final Context context, final String url, final String pathName, final GameLoadCallback callback) {
        final Future<File> ff = runnableExecutor.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                int count;
                URL uri = new URL(url);
                File file = new File(context.getFilesDir() + "/zip/" + pathName + "/"+ url.hashCode() + ".zip");

                //TODO remove after tests
               /* if (file.exists()) {
                    File parentFolder = file.getParentFile();
                    if (parentFolder != null && parentFolder.exists()) {
                        deleteFolderRecursive(parentFolder, true);
                    }
                }*/

                if (file.exists())
                    return file;
                try {
                    file.mkdirs();
                } catch (Exception e) {

                }
                if (file.exists()) file.delete();
                File parentFolder = file.getParentFile();
                if (parentFolder != null && parentFolder.exists()) {
                    deleteFolderRecursive(parentFolder, false);
                }
                URLConnection connection = uri.openConnection();
                connection.connect();
                int sz = connection.getContentLength();
                InputStream input = new BufferedInputStream(uri.openStream(),
                        8192);
                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                int cnt = 0;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                    cnt += count;
                    callback.onProgress(cnt, sz);
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
