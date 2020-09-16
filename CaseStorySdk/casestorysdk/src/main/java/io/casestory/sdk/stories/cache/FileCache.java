package io.casestory.sdk.stories.cache;

import android.content.Context;
import android.util.LruCache;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public enum FileCache {

    //TODO лимиты, очистка папок.

    INSTANCE;

    private final LruCache<String, String> hashed = new LruCache<>(64);

    /**
     * Перемещение файла из temp хранилища в папку в зависимости от {@param type}
     *
     * @param context - контекст приложения
     * @param uri     - ссылка на файл в папке
     * @param type    - тип файла (issue, rss, article)
     * @param id      - id записи
     * @return Возвращаем файл (путь), в который переместили или null, если не нашли
     * файла по {@param type}, {@param id} и {@param uri}
     */

    public File moveFileToStorage(Context context, String uri, String type, Integer id, String ext) {
        File outFile = getStoredFile(context, uri, type, id, ext);
        outFile.getParentFile().mkdirs();
        File file = getStoredFile(context, uri, FileType.TEMP_FILE, null, ext);
        if (file.exists()) {
            file.renameTo(outFile);
            return file;
        }
        return null;
    }


    public File saveFile(Context context, String uri, Integer id, String type, byte[] bytes, String ext) {

        FileOutputStream fout = null;
        File img = getStoredFile(context, uri, type, id, ext);
        if (inStorage(context, uri, type, id)) {
            return img;
        }
        File file = moveFileToStorage(context, uri, type, id, ext);
        if (file != null) return file;
        if (bytes == null) {
            return img;
        }
        try {
            fout = new FileOutputStream(img);
            fout.write(bytes);
            fout.close();
            return img;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (Exception e) {/**/}
            return img;
        }
    }

    /**
     * Удаление файла из папки в зависимости {@param type}
     *
     * @param context - контекст приложения
     * @param uri     - ссылка на файл в папке
     * @param type    - тип файла (issue, rss, article)
     * @param id      - id записи
     */

    public void deleteStorageFile(Context context, String uri, String type, Integer id, String ext) {
        File file = getStoredFile(context, uri, type, id, ext);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Получаем список файлов в папке (для временных)
     *
     * @param context         - контекст приложения
     * @param fileOrDirectory - путь к файлу или папке
     * @return возвращаем размер (количество байт) файлов
     */

    public ArrayList<File> getTempFiles(Context context, File fileOrDirectory) {
        if (fileOrDirectory == null) {
            fileOrDirectory = new File(context.getFilesDir(), File.separator + "temp" + File.separator);
        }
        if (!fileOrDirectory.exists()) return new ArrayList<>();
        ArrayList res = new ArrayList();
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                res.addAll(getTempFiles(context, child));
        } else {
            res.add(fileOrDirectory);
        }
        return res;
    }

    /**
     * Получаем список файлов в папке (для скачанных)
     *
     * @param context         - контекст приложения
     * @param fileOrDirectory - путь к файлу или папке
     * @return возвращаем размер (количество байт) файлов
     */

    public ArrayList<File> getDownloadsFiles(Context context, File fileOrDirectory) {
        String uid = "";
        if (fileOrDirectory == null) {
            fileOrDirectory = new File(context.getFilesDir(), uid + File.separator + "stored" + File.separator);
        }
        if (!fileOrDirectory.exists()) return new ArrayList<>();
        ArrayList res = new ArrayList();
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                res.addAll(getDownloadsFiles(context, child));
        } else {
            res.add(fileOrDirectory);
        }
        return res;
    }


    /**
     * Удаление всех файлов в папке (и самой папки)
     *
     * @param fileOrDirectory - путь к файлу или папке
     */

    void deleteRecursive(File fileOrDirectory) {
        if (!fileOrDirectory.exists()) return;
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    /**
     * Очистка папки конкретного инстанса (журнала / статьи)
     *
     * @param context - контекст приложения
     * @param type    - тип (issue, rss, article)
     * @param id      - id записи
     */

    public void clearFolder(Context context, String type, Integer id) {
        File directory = context.getFilesDir();
        String uid = "";
        switch (type) {
            case FileType.Story_IMAGE:
                directory = new File(directory, File.separator + "Stories" + File.separator);
                if (!directory.exists() || directory.listFiles() == null) return;
                deleteRecursive(directory);
                break;
            case FileType.STORED_FILE:
                directory = new File(directory, uid + File.separator + "stored" + File.separator);
                if (!directory.exists() || directory.listFiles() == null) return;
                for (File child : directory.listFiles())
                    deleteRecursive(child);
                break;
            case FileType.TEMP_FILE:
                directory = new File(directory, File.separator + "temp" + File.separator);
                if (!directory.exists() || directory.listFiles() == null) return;
                for (File child : directory.listFiles())
                    deleteRecursive(child);
                break;
        }
    }

    /**
     * Сохранен ли файл в систему
     *
     * @param context - контекст приложения
     * @param uri     - удаленная ссылка на файл (на сервере)
     * @param type    - (issue, rss, article)
     * @param id      - id записи
     * @return сохранен ли файл в систему
     */

    public boolean inStorage(Context context, String uri, String type, Integer id) {
        return getStoredFile(context, uri, type, id, null).exists();
    }

    /**
     * Получаем полный путь к файлу по url из temp
     *
     * @param context - контекст приложения
     * @param uri     - удаленная ссылка на файл (на сервере)
     * @return возвращаем ссылку на локальный файл
     */

    private File getTempFile(Context context, String uri, String ext) {
        String fName = hashed.get(uri);
        if (fName == null) {
            fName = hash(uri);

            hashed.put(uri, fName);
        }
        File dir = context.getFilesDir();
        if (ext != null) {
            fName += "." + ext;
        } else {
            fName = getFileNameWithoutExt(context, fName, new File(dir.getAbsolutePath() + File.separator + "temp"));
        }
        fName = File.separator + "temp" + File.separator + fName;

        return new File(dir, fName);
    }

    private String getFileNameWithoutExt(Context context, String fName, File dir) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return fName;
        for (File file : files) {
            if (file.isFile() && file.getName().contains(fName)) {
                return file.getName();
            }
        }
        return fName;
    }

    @NonNull
    public static String hash(@NonNull String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA512");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString() + "_u0";

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Получаем полный путь к файлу по url из temp
     *
     * @param context - контекст приложения
     * @param uri1    - удаленная ссылка на файл (на сервере)
     * @param type    - (issue, rss, article)
     * @param id      - id записи
     * @return возвращаем ссылку на локальный файл
     */

    public File getStoredFile(Context context, String uri1, String type, Integer id, String ext) {
        String uri = Downloader.cropUrl(uri1);
        switch (type) {
            case FileType.Story_IMAGE:
                return getSavedStoryImage(context, uri, id, ext);
            case FileType.TEMP_FILE:
                return getTempFile(context, uri, ext);
            default: {
                String fName = hashed.get(uri);
                if (fName == null) {
                    fName = hash(uri);
                    hashed.put(uri, fName);
                }
                File dir = context.getFilesDir();
                if (ext != null) {
                    fName += "." + ext;
                } else {
                    fName = getFileNameWithoutExt(context, fName, dir);
                }
                return new File(dir, fName);
            }
        }
    }

    private File getSavedStoryImage(Context context, String uri1, int StoryId, String ext) {
        String uri = Downloader.cropUrl(uri1);
        String fName = hashed.get(uri);
        if (fName == null) {
            fName = hash(uri);
            hashed.put(uri, fName);
        }
        File dir = context.getFilesDir();
        if (ext != null) {
            fName += "." + ext;
        } else {
            fName = getFileNameWithoutExt(context, fName, new File(dir.getAbsolutePath() + File.separator + "Stories" + File.separator + "Story_" + StoryId));
        }
        fName = File.separator + "Stories" + File.separator + "Story_" + StoryId + File.separator + fName;

        return new File(dir, fName);
    }
}
