package com.inappstory.sdk.game.loader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.statistic.ProfilingManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.UUID.randomUUID;

public class GameLoader {

    private static final String INDEX_MANE = "/index.html";
    public static final String FILE = "file://";

    private GameLoader() {

    }

    private static volatile GameLoader INSTANCE;


    public static GameLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (GameLoader.class) {
                if (INSTANCE == null)
                    INSTANCE = new GameLoader();
            }
        }
        return INSTANCE;
    }

    private static final ExecutorService gameFileThread = Executors.newFixedThreadPool(1);

    private String getStringFromFile(File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private void deleteFolderRecursive(File fileOrDirectory, boolean deleteRoot) {

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

    private boolean downloadResources(final List<WebResource> resources,
                                      final File file,
                                      final GameLoadCallback callback,
                                      final int totalSize,
                                      final int curSize) {
        if (terminate) return false;
        if (InAppStoryService.isNull()) return false;
        String pathName = file.getAbsolutePath();
        final File filePath = new File(pathName + "/src/");
        //  if (!filePath.exists()) {
        //      filePath.mkdirs();
        //  }
        int cnt = curSize;
        boolean downloaded = false;
        for (WebResource resource : resources) {
            if (terminate) return false;
            try {
                String url = resource.url;
                String fileName = resource.key;
                if (url == null || url.isEmpty() || fileName == null || fileName.isEmpty())
                    continue;
                downloaded |= Downloader.downloadOrGetGameFile(url, fileName, InAppStoryService.getInstance().getCommonCache(),
                        new File(filePath.getAbsolutePath() + "/" + fileName),
                        null);
                cnt += resource.size;
                if (callback != null)
                    callback.onProgress(cnt, totalSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (callback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    File fl = new File(file.getAbsolutePath() + INDEX_MANE);
                    try {
                        callback.onLoad(FILE + fl.getAbsolutePath(), getStringFromFile(fl));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return downloaded;
    }

    public void downloadAndUnzip(final Context context,
                                 final List<WebResource> resources,
                                 final String url,
                                 final String pathName,
                                 final GameLoadCallback callback) {
        if (InAppStoryService.isNull()) return;
        terminate = false;
        gameFileThread.submit(new Callable() {
            @Override
            public Void call() {
                try {
                    if (InAppStoryService.isNull()) return null;
                    int totalSize = 0;
                    for (WebResource resource : resources) {
                        totalSize += resource.size;
                    }
                    final int fTotalSize = totalSize;
                    String hash = randomUUID().toString();
                    File file = Downloader.downloadOrGetFile(url,
                            InAppStoryService.getInstance().getCommonCache(),
                            new File(InAppStoryService.getInstance().getCommonCache().getCacheDir() +
                                    File.separator + "zip" +
                                    File.separator + pathName +
                                    File.separator + url.hashCode() + ".zip"),
                            new FileLoadProgressCallback() {
                                @Override
                                public void onProgress(int loadedSize, int totalSize) {
                                    callback.onProgress(loadedSize, fTotalSize + totalSize);
                                }

                                @Override
                                public void onSuccess(File downloadedFile) {

                                }
                            }, hash);

                    ProfilingManager.getInstance().setReady(hash);
                    File directory = new File(file.getParent() + File.separator + url.hashCode());
                    String resourcesHash;
                    if (directory.exists()) {
                        resourcesHash = ProfilingManager.getInstance().addTask("game_resources_download");
                        if (downloadResources(resources, directory, callback, fTotalSize + (int) file.length(),
                                (int) file.length()))
                            ProfilingManager.getInstance().setReady(resourcesHash);
                        if (InAppStoryService.getInstance().getCommonCache().get(directory.getName()) == null) {
                            InAppStoryService.getInstance().getCommonCache().put(directory.getName(), directory);
                        }
                    } else if (file.exists()) {
                        String unzipHash = ProfilingManager.getInstance().addTask("game_unzip");
                        FileUnzipper.unzip(file, directory);
                        ProfilingManager.getInstance().setReady(unzipHash);
                        InAppStoryService.getInstance().getCommonCache().put(directory.getName(), directory);
                        resourcesHash = ProfilingManager.getInstance().addTask("game_resources_download");
                        if (downloadResources(resources, directory, callback, fTotalSize + (int) file.length(),
                                (int) file.length()))
                            ProfilingManager.getInstance().setReady(resourcesHash);
                    } else {
                        if (callback != null)
                            callback.onError();
                    }
                } catch (Exception e) {
                    if (callback != null)
                        callback.onError();
                }
                return null;
            }
        });
    }

    boolean terminate = false;

    public void terminate() {
        terminate = true;
    }

    private int downloadStream(URL uri, File file, GameLoadCallback callback, int startSize, int totalSize) {
        try {
            int count;
            InputStream input = new BufferedInputStream(uri.openStream(),
                    8192);
            OutputStream output = new FileOutputStream(file);
            byte data[] = new byte[1024];
            int cnt = startSize;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                cnt += count;
                callback.onProgress(cnt, totalSize);
            }
            output.flush();
            output.close();
            input.close();
            return cnt;
        } catch (Exception e) {
            if (file.exists())
                file.delete();
            return startSize;
        }
    }
}
