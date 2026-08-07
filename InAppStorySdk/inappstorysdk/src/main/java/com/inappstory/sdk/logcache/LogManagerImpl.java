package com.inappstory.sdk.logcache;

import com.inappstory.sdk.core.IASCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

public class LogManagerImpl implements LogManager {
    private final IASCore core;
    private final String filename;

    public LogManagerImpl(IASCore core, String filename) {
        this.core = core;
        this.filename = System.currentTimeMillis() + "";;
        archiveLogs();
    }


    private final ExecutorService saveToFileThread = Executors.newFixedThreadPool(1);

    @Override
    public void saveLog(final String tag, final String message) {
        saveToFileThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File dir = new File(core.appContext().getFilesDir() + File.separator + "logs");
                    if (!dir.exists())
                        dir.mkdir();
                    File logFile = new File(core.appContext().getFilesDir() + File.separator + "logs", filename);
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                    FileWriter fOut = new FileWriter(logFile, true);
                    fOut.write(tag + "\t\t" + message);
                    fOut.close();
                } catch (IOException e) {
                }
            }
        });
    }

    @Override
    public void sendLogs() {

    }

    @Override
    public void clearOldLogs() {

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

    @Override
    public void archiveLogs() {
        File dir = new File(core.appContext().getFilesDir() + File.separator + "logs");
        if (!dir.exists())
            dir.mkdir();
        saveToFileThread.execute(new Runnable() {
            @Override
            public void run() {
                File[] files = dir.listFiles();
                if (files != null)
                    for (File file : files) {
                        if (file.getName().equals(filename)) continue;
                        if (file.getName().endsWith(".gz")) continue;
                        zip(file.getAbsolutePath());
                        file.delete();
                    }
            }
        });
    }
}
