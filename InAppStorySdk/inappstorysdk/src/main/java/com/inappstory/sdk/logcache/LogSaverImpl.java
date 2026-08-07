package com.inappstory.sdk.logcache;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.impl.IASSettingsImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

public class LogSaverImpl implements LogSaver {
    private final IASCore core;
    private final ExecutorService workThread = Executors.newSingleThreadExecutor();
    private final File dir;

    private String filename = System.currentTimeMillis() + "";

    public LogSaverImpl(IASCore core) {
        this.core = core;
        dir = new File(core.appContext().getFilesDir() + File.separator + "logs");
        if (!dir.exists())
            dir.mkdir();
        workThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    archiveOldFiles();
                    clearOldFiles();
                } catch (IOException e) {

                }
            }
        });
    }

    private void changeFileName() {
        this.filename = System.currentTimeMillis() + "";
    }


    @Override
    public void saveLog(String tag, String message) {
        IASSettingsImpl settings = (IASSettingsImpl) core.settingsAPI();
        workThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File logFile = getOrCreateLogFile();
                    FileWriter fOut = new FileWriter(logFile, true);
                    long ms = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    fOut.write(
                            settings.userId() +
                                    "\t\t" +
                                    settings.deviceId() +
                                    "\t\t" +
                                    sdf.format(new Date(ms)) +
                                    "\t\t" +
                                    tag +
                                    "\t\t" +
                                    message
                    );
                    fOut.close();
                } catch (IOException e) {
                }
            }
        });
    }

    private void clearOldFiles() {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                Log.e("LastModifiedDate", file.lastModified() + "");
            }
        }
    }

    @Override
    public void prepareFiles() {
        try {
            archiveOldFiles();
        } catch (IOException e) {
        }
    }

    @Override
    public List<String> getFiles() {
        File[] files = dir.listFiles();
        List<String> filePaths = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().endsWith(".gz")) continue;
                filePaths.add(file.getAbsolutePath());
            }
        }
        return filePaths;
    }

    private void archiveOldFiles() throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equals(filename)) continue;
                if (file.getName().endsWith(".gz")) continue;
                zip(file.getAbsolutePath());
                file.delete();
            }
        }
    }

    private File getOrCreateLogFile() throws IOException {
        File logFile = new File(
                core.appContext().getFilesDir() + File.separator + "logs",
                filename
        );
        if (!logFile.exists()) {
            logFile.createNewFile();
        } else if (logFile.length() > 5000000) {
            archiveOldFiles();
            changeFileName();
            return getOrCreateLogFile();
        }
        return logFile;
    }

    private void zip(String filename) {
        try {
            File file = new File(filename);
            File gzFile = new File(file.getParentFile(), file.getName() + ".gz");
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(gzFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
            gzipOS.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
