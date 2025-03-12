package com.inappstory.sdk.stories.statistic;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesAPI {
    public static void setContext(Context context) {

        synchronized (contextLock) {
            SharedPreferencesAPI.context = context;
        }
    }

    private static Context context;

    public static boolean hasContext() {

        synchronized (contextLock) {
            return context != null;
        }
    }

    private static final String SHARED_PREFERENCES_DEFAULT = "default_n";


    public static SharedPreferences getDefaultPreferences() {
        synchronized (contextLock) {
            if (context == null) return null;
            return context.getSharedPreferences(SHARED_PREFERENCES_DEFAULT, Context.MODE_PRIVATE);
        }
    }

    /**
     * Сохранение строки
     */
    private static Object sharedPrefLock = new Object();
    private static Object contextLock = new Object();


    public static void saveString(final String key, final String value) {

        synchronized (contextLock) {
            if (context == null) return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (sharedPrefLock) {
                    SharedPreferences.Editor editor = getDefaultPreferences().edit();
                    editor.putString(key, value);
                    editor.commit();
                }
            }
        }).start();
    }


    /**
     * Получение строки
     */
    public static String getString(String key) {
        synchronized (sharedPrefLock) {
            SharedPreferences preferences = getDefaultPreferences();
            if (preferences == null) return null;
            return preferences.getString(key, null);
        }
    }

    /**
     * Получение строки
     */
    public static void removeString(String key) {

        synchronized (contextLock) {
            if (context == null) return;
        }
        synchronized (sharedPrefLock) {
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
            SharedPreferences preferences = getDefaultPreferences();
            if (preferences == null) return null;
            return preferences.getString(key, def);
        }
    }

    /**
     * Сохранение массива строк
     */
    public static void saveStringSet(final String key, final Set<String> value) {

        synchronized (contextLock) {
            if (context == null) return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (sharedPrefLock) {
                    SharedPreferences.Editor editor = getDefaultPreferences().edit();
                    editor.putStringSet(key, value);
                    boolean isCommitted = editor.commit();
                }
            }
        }).start();

    }


    /**
     * Получение массива строк
     */
    public static Set<String> getStringSet(String key) {
        synchronized (sharedPrefLock) {
            SharedPreferences preferences = getDefaultPreferences();
            if (preferences == null) return null;
            Set<String> resSet = preferences.getStringSet(key, null);
            if (resSet != null) return new HashSet<>(resSet);
            return null;
        }
    }


    /**
     * Удаление значения по ключу
     */
    public static void remove(final String key) {

        synchronized (contextLock) {
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