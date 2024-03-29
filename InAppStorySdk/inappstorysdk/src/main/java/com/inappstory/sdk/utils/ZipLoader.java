package com.inappstory.sdk.utils;

import static java.util.UUID.randomUUID;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.lrudiskcache.FileManager;
import com.inappstory.sdk.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.cache.DownloadFileState;
import com.inappstory.sdk.stories.cache.DownloadInterruption;
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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZipLoader {

    private static final String INDEX_NAME = "/index.html";
    public static final String FILE = "file://";

    private ZipLoader() {

    }

    private static ZipLoader INSTANCE;
    private static final Object lock = new Object();

    public static String[] urlParts(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        return fName.split("_");
    }

    public static ZipLoader getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new ZipLoader();
            return INSTANCE;
        }
    }

    private static final ExecutorService downloadFileThread = Executors.newFixedThreadPool(1);

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

    private void deleteFileIfNotPass(File file) {
        if (file.exists()) file.delete();
    }

    private boolean downloadResources(final List<WebResource> resources,
                                      final File file,
                                      final ZipLoadCallback callback,
                                      final String instanceId,
                                      final long totalSize,
                                      final long curSize) {
        if (resources == null) return true;
        if (terminate) return false;
        if (InAppStoryService.isNull()) return false;
        String pathName = file.getAbsolutePath();
        final File filePath = new File(
                pathName +
                        File.separator +
                        (instanceId != null ? ("resources_" + instanceId) : "src") +
                        File.separator
        );
        long cnt = curSize;
        boolean downloaded = false;
        for (WebResource resource : resources) {
            if (terminate) return false;
            try {
                String url = resource.url;
                String fileName = resource.key;
                if (url == null || url.isEmpty() || fileName == null || fileName.isEmpty())
                    continue;
                File resourceFile = new File(filePath.getAbsolutePath() + "/" + fileName);
                if (resourceFile.exists()) {
                    if (!FileManager.checkShaAndSize(resourceFile, resource.size, resource.sha1)) {
                        deleteFileIfNotPass(resourceFile);
                    } else {
                        cnt += resource.size;
                        if (callback != null)
                            callback.onProgress(cnt, totalSize);
                        continue;
                    }
                }
                downloaded |= Downloader.downloadOrGetResourceFile(url, fileName, InAppStoryService.getInstance().getInfiniteCache(),
                        resourceFile,
                        null);
                if (!FileManager.checkShaAndSize(resourceFile, resource.size, resource.sha1)) {
                    deleteFileIfNotPass(resourceFile);
                } else {
                    cnt += resource.size;
                    if (callback != null)
                        callback.onProgress(cnt, totalSize);
                }
            } catch (Exception e) {
                InAppStoryService.createExceptionLog(e);
                e.printStackTrace();
            }
        }

        if (callback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    File fl = new File(file.getAbsolutePath() + INDEX_NAME);
                    try {
                        if (fl.exists())
                            callback.onLoad(FILE + fl.getAbsolutePath(), getStringFromFile(fl));
                        else
                            callback.onError("No index file");
                    } catch (Exception e) {
                        InAppStoryService.createExceptionLog(e);
                        e.printStackTrace();
                    }
                }
            });
        }
        return downloaded;
    }


    public void downloadAndUnzip(final List<WebResource> resources,
                                 final String url,
                                 final String pathName,
                                 final ZipLoadCallback callback,
                                 final String profilingPrefix) {
        downloadAndUnzip(resources, url, pathName, null, null, callback, null, profilingPrefix);
    }

    public void downloadAndUnzip(final List<WebResource> resources,
                                 String outUrl,
                                 final String pathName,
                                 final String instanceId,
                                 final GameCenterData gameCenterData,
                                 final ZipLoadCallback callback,
                                 final DownloadInterruption interruption,
                                 final String profilingPrefix) {
        final String url = outUrl.split("\\?")[0];
        terminate = false;
        downloadFileThread.submit(new Callable() {
            @Override
            public Void call() {
                try {
                    InAppStoryService inAppStoryService = InAppStoryService.getInstance();
                    if (inAppStoryService == null) {
                        if (callback != null) {
                            callback.onError("InAppStoryService is not created");
                        }
                        return null;
                    }
                    LruDiskCache cache = inAppStoryService.getInfiniteCache();
                    if (pathName.contains("\\") || pathName.contains("/")) return null;
                    File gameDir = new File(
                            inAppStoryService.getInfiniteCache().getCacheDir() +
                                    File.separator + "zip" +
                                    File.separator + pathName +
                                    File.separator
                    );
                    File getFile = new File(gameDir, url.hashCode() + ".zip");
                    if (!getFile.getAbsolutePath().startsWith(
                            cache.getCacheDir() +
                                    File.separator + "zip")) return null;
                    long totalSize = 0;
                    if (resources != null)
                        for (WebResource resource : resources) {
                            totalSize += resource.size;
                        }

                    final long fTotalSize = totalSize;


                    String hash = randomUUID().toString();

                    if (gameDir.exists() && gameDir.isDirectory()) {
                        for (File gameDirFile : gameDir.listFiles()) {
                            if (gameDirFile.getAbsolutePath().contains("" + url.hashCode()))
                                continue;
                            deleteFolderRecursive(gameDirFile, true);
                        }
                    }
                    File cachedArchive = InAppStoryService.getInstance().getInfiniteCache().getFullFile(
                            Downloader.cropUrl(url, true)
                    );
                    if (cachedArchive != null) {
                        if (gameCenterData != null &&
                                !FileManager.checkShaAndSize(cachedArchive, gameCenterData.archiveSize, gameCenterData.archiveSha1)
                        ) {
                            InAppStoryService.getInstance().getInfiniteCache().delete(url);
                            cachedArchive = null;
                            File directory = new File(
                                    getFile.getParent() +
                                            File.separator + url.hashCode());
                            if (directory.exists()) {
                                deleteFolderRecursive(directory, true);
                            }
                        }
                    }
                    DownloadFileState fileState;
                    if (cachedArchive == null || !cachedArchive.exists()) {
                        if (gameCenterData != null && gameCenterData.getTotalSize() >
                                cache.getCacheDir().getFreeSpace()) {
                            callback.onError("No free space for download");
                            return null;
                        }
                        fileState = Downloader.downloadOrGetFile(
                                url,
                                true,
                                InAppStoryService.getInstance().getInfiniteCache(),
                                getFile,
                                new FileLoadProgressCallback() {
                                    @Override
                                    public void onProgress(long loadedSize, long totalSize) {
                                        callback.onProgress(loadedSize, (long) (1.2f * (fTotalSize + totalSize)));
                                    }

                                    @Override
                                    public void onSuccess(File file) {

                                    }

                                    @Override
                                    public void onError(String error) {
                                        if (callback != null)
                                            callback.onError(error);
                                    }
                                },
                                interruption,
                                hash
                        );
                        if (fileState != null && fileState.file != null && (fileState.downloadedSize == fileState.totalSize)) {
                            getFile = fileState.file;
                        } else {
                            getFile = null;
                        }
                    }

                    if (getFile == null || !getFile.exists()) {
                        if (callback != null)
                            callback.onError("File download interrupted");
                        return null;
                    } else {
                        if (gameCenterData != null &&
                                !FileManager.checkShaAndSize(getFile, gameCenterData.archiveSize, gameCenterData.archiveSha1)
                        ) {
                            getFile.delete();
                            if (callback != null)
                                callback.onError("Wrong file sha or size");
                            return null;
                        }
                    }
                    ProfilingManager.getInstance().setReady(hash);
                    File directory = new File(
                            getFile.getParent() +
                                    File.separator + url.hashCode());
                    String resourcesHash;
                    final long allFilesSize = fTotalSize + getFile.length();

                    if (directory.exists()) {
                        resourcesHash = ProfilingManager.getInstance().addTask(
                                profilingPrefix + "_resources_download");
                        if (downloadResources(
                                resources,
                                directory,
                                callback,
                                instanceId,
                                (long) (1.2 * allFilesSize),
                                getFile.length() + (long) (0.2 * allFilesSize)
                        ))
                            ProfilingManager.getInstance().setReady(resourcesHash);
                        if (InAppStoryService.getInstance().getInfiniteCache().get(directory.getName()) == null) {
                            InAppStoryService.getInstance().getInfiniteCache().put(directory.getName(), directory);
                        }
                    } else if (getFile.exists()) {
                        String unzipHash = ProfilingManager.getInstance().addTask(profilingPrefix + "_unzip");
                        final File finalGetFile = getFile;
                        FileUnzipper.unzip(getFile, directory, new ProgressCallback() {
                            @Override
                            public void onProgress(long loadedSize, long totalSize) {
                                callback.onProgress(
                                        finalGetFile.length() +
                                                (long) (0.2f * allFilesSize * loadedSize / totalSize),
                                        (long) (1.2 * allFilesSize)
                                );
                            }
                        });
                        ProfilingManager.getInstance().setReady(unzipHash);
                        InAppStoryService.getInstance().getInfiniteCache().put(directory.getName(), directory);
                        resourcesHash = ProfilingManager.getInstance().addTask(
                                profilingPrefix + "_resources_download");
                        if (downloadResources(
                                resources,
                                directory,
                                callback,
                                instanceId,
                                (long) (1.2 * allFilesSize),
                                getFile.length() + (long) (0.2 * allFilesSize)
                        ))
                            ProfilingManager.getInstance().setReady(resourcesHash);
                    } else {
                        if (callback != null)
                            callback.onError("Zip file or unzipped directory not exists");
                    }
                } catch (Exception e) {
                    InAppStoryService.createExceptionLog(e);
                    e.printStackTrace();
                    if (callback != null)
                        callback.onError(e.getMessage());
                }
                return null;
            }
        });
    }

    boolean terminate = false;

    public void terminate() {
        terminate = true;
    }

    private int downloadStream(URL uri, File file, ZipLoadCallback callback, int startSize, int totalSize) {
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
            InAppStoryService.createExceptionLog(e);
            if (file.exists())
                file.delete();
            return startSize;
        }
    }
}
