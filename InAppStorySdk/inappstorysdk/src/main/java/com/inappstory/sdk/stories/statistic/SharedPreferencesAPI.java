package com.inappstory.sdk.stories.statistic;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPreferencesAPI {
    public static void setContext(Context context) {
        synchronized (sharedPrefLock) {
            SharedPreferencesAPI.context = context;
        }
    }

    private static Context context;

    public static boolean hasContext() {
        synchronized (sharedPrefLock) {
            return context != null;
        }
    }

    private static final String SHARED_PREFERENCES_DEFAULT = "default_n";

    public static SharedPreferences getDefaultPreferences() {
        if (context == null) return null;
        return context.getSharedPreferences(SHARED_PREFERENCES_DEFAULT, Context.MODE_PRIVATE);
    }

    /**
     * Сохранение строки
     */
    private static Object sharedPrefLock = new Object();


    public static void saveString(final String key, final String value) {
        synchronized (sharedPrefLock) {
            if (context == null) return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (sharedPrefLock) {
                    SharedPreferences.Editor editor = getDefaultPreferences().edit();
                    editor.putString(key, value);
                    editor.apply();
                }
            }
        }).start();
    }


    /**
     * Получение строки
     */
    public static String getString(String key) {
        synchronized (sharedPrefLock) {
            SharedPreferences prefs = getDefaultPreferences();
            if (prefs == null) return null;
            return prefs.getString(key, null);
        }
    }

    /**
     * Получение строки
     */
    public static void removeString(String key) {
        synchronized (sharedPrefLock) {
            if (context == null) return;
            SharedPreferences.Editor editor = getDefaultPreferences().edit();
            editor.remove(key);
            editor.apply();
        }
    }

    /**
     * Получение строки
     */
    public static String getString(String key, String def) {
        synchronized (sharedPrefLock) {
            SharedPreferences prefs = getDefaultPreferences();
            if (prefs == null) return null;
            return prefs.getString(key, def);
        }
    }

    /**
     * Сохранение массива строк
     */
    public static void saveStringSet(final String key, final Set<String> value) {
        synchronized (sharedPrefLock) {
            if (context == null) return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (sharedPrefLock) {
                    SharedPreferences.Editor editor = getDefaultPreferences().edit();
                    editor.putStringSet(key, value);
                    editor.apply();
                }
            }
        }).start();

    }


    /**
     * Получение массива строк
     */
    public static Set<String> getStringSet(String key) {
        synchronized (sharedPrefLock) {
            SharedPreferences prefs = getDefaultPreferences();
            if (prefs == null) return null;
            return prefs.getStringSet(key, null);
        }
    }

    /**
     * Удаление значения по ключу
     */
    public static void remove(final String key) {
        synchronized (sharedPrefLock) {
            if (context == null) return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (sharedPrefLock) {
                    SharedPreferences.Editor editor = getDefaultPreferences().edit();
                    editor.remove(key);
                    editor.apply();
                }
            }
        }).start();


    }

    /**
     * Очистка SharedPreferences
     */
    public static void clear() {
        synchronized (sharedPrefLock) {
            getDefaultPreferences().edit().clear().apply();
        }
    }
}