package io.casestory.sdk.stories.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;

import io.casestory.sdk.CaseStoryManager;

public class KeyValueStorage {


    public static void setContext(Context context) {
        KeyValueStorage.context = context;
    }

    private static Context context;

    private static final String SHARED_PREFERENCES_DEFAULT = "default_n";

    public static SharedPreferences getDefaultPreferences() {
        if (context == null)
            context = CaseStoryManager.getInstance().getContext();
        if (context == null) return null;
        return context.getSharedPreferences(SHARED_PREFERENCES_DEFAULT, Context.MODE_PRIVATE);
    }

    /**
     * Сохранение строки
     */
    public static void saveString(String key, String value) {
        if (getDefaultPreferences() == null) return;
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
        if (getDefaultPreferences() == null) return;
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
     * Сохранение json объекта
     */
    public static void saveObject(String key, Object value) {
        if (getDefaultPreferences() == null) return;
        SharedPreferences.Editor editor = getDefaultPreferences().edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(value);
        editor.putString(key, jsonString);
        editor.apply();
    }

    /**
     * Получение json объекта
     */
    public static <T> T getObject(String key, Class<T> type) {
        if (getDefaultPreferences() == null) return null;
        Gson gson = new Gson();
        String jsonString = getDefaultPreferences().getString(key, null);
        if (jsonString != null) {
            return gson.fromJson(jsonString, type);
        }
        return null;
    }

    /**
     * Получение json объекта
     */
    public static <T> T getObject(String key, Type type) {
        if (getDefaultPreferences() == null) return null;
        Gson gson = new Gson();
        String jsonString = getDefaultPreferences().getString(key, null);
        if (jsonString != null) {
            return gson.fromJson(jsonString, type);
        }
        return null;
    }

    /**
     * Сохранение json объекта
     */
    public static void saveMap(String key, HashMap value) {
        SharedPreferences.Editor editor = getDefaultPreferences().edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(value);
        editor.putString(key, jsonString);
        editor.apply();
    }

    /**
     * Получение json объекта
     */
    public static <T> T getMap(String key, Class<T> type) {
        if (getDefaultPreferences() == null) return null;
        Gson gson = new Gson();
        String jsonString = getDefaultPreferences().getString(key, null);
        if (jsonString != null) {
            return gson.fromJson(jsonString, type);
        }
        return null;
    }

    /**
     * Получение json объекта
     */
    public static <T> T getMap(String key, Type type) {
        if (getDefaultPreferences() == null) return null;
        Gson gson = new Gson();
        String jsonString = getDefaultPreferences().getString(key, null);
        if (jsonString != null) {
            return gson.fromJson(jsonString, type);
        }
        return null;
    }
}
