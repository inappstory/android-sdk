package com.inappstory.sdk.stories.statistic;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SharedPreferencesAPI {
    public static void setContext(Context context) {
        SharedPreferencesAPI.context = context;
    }

    private static Context context;

    public static boolean hasContext() {
        return context != null;
    }

    private static final String SHARED_PREFERENCES_DEFAULT = "default_n";



    public static SharedPreferences getDefaultPreferences() {
        if (context == null) return null;
        return context.getSharedPreferences(SHARED_PREFERENCES_DEFAULT, Context.MODE_PRIVATE);
    }

    /**
     * Сохранение строки
     */
    public static void saveString(String key, String value) {
        if (context == null) return;
        SharedPreferences.Editor editor = getDefaultPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }


    /**
     * Получение строки
     */
    public static String getString(String key) {
        if (getDefaultPreferences() == null) return null;
        return getDefaultPreferences().getString(key, null);
    }

    /**
     * Получение строки
     */
    public static void removeString(String key) {
        if (context == null) return;
        SharedPreferences.Editor editor = getDefaultPreferences().edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * Получение строки
     */
    public static String getString(String key, String def) {
        if (getDefaultPreferences() == null) return null;
        return getDefaultPreferences().getString(key, def);
    }

    /**
     * Сохранение массива строк
     */
    public static void saveStringSet(String key, Set<String> value) {
        if (context == null) return;
        SharedPreferences.Editor editor = getDefaultPreferences().edit();
        editor.putStringSet(key, value);
        editor.apply();
    }


    /**
     * Получение массива строк
     */
    public static Set<String> getStringSet(String key) {
        if (getDefaultPreferences() == null) return null;
        return getDefaultPreferences().getStringSet(key, null);
    }


    /**
     * Сохранение boolean значения
     */
    public static void saveBoolean(String key, boolean value) {
        if (context == null) return;
        SharedPreferences.Editor editor = getDefaultPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Сохранение числового значения
     */
    public static void saveInt(String key, int value) {
        if (context == null) return;
        SharedPreferences.Editor editor = getDefaultPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }


    /**
     * Удаление значения по ключу
     */
    public static void remove(String key) {
        if (context == null) return;
        SharedPreferences.Editor editor = getDefaultPreferences().edit();
        editor.remove(key);
        editor.apply();
    }


    /**
     * Получение числового значения
     */
    public static int getInt(String key) {
        return getDefaultPreferences().getInt(key, -1);
    }

    /**
     * Получение числового значения
     */
    public static int getInt(String key, int defaultVal) {
        if (getDefaultPreferences() == null) return -1;
        return getDefaultPreferences().getInt(key, defaultVal);
    }

    /**
     * Получение boolean значения
     */
    public static boolean getBoolean(String key) {
        if (getDefaultPreferences() == null) return false;
        return getDefaultPreferences().getBoolean(key, false);
    }

    /**
     * Получение boolean значения
     */
    public static boolean getBoolean(String key, boolean defValue) {
        if (getDefaultPreferences() == null) return false;
        return getDefaultPreferences().getBoolean(key, defValue);
    }


    /**
     * Сохранение json объекта
     */
    public static long getLong(String key, long defaultVal) {
        if (getDefaultPreferences() == null) return 0l;
        return getDefaultPreferences().getLong(key, defaultVal);
    }


    /**
     * Сохранение long
     */
    public static void saveLong(String key, long value) {
        getDefaultPreferences().edit().putLong(key, value).apply();
    }

    /**
     * Очистка SharedPreferences
     */
    public static void clear() {
        getDefaultPreferences().edit().clear().apply();
    }
}