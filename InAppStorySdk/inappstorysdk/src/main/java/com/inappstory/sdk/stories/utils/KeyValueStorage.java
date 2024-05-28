package com.inappstory.sdk.stories.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.network.JsonParser;

import java.util.HashMap;

public class KeyValueStorage {


    public static void setContext(Context context) {
        KeyValueStorage.context = context;
    }

    private static Context context;

    private static final String SHARED_PREFERENCES_DEFAULT = "key_value_prefs";

    private static SharedPreferences getKeyValuePrefs() {
        if (context == null) {

            InAppStoryService service = InAppStoryService.getInstance();
            if (service == null) return null;
            context = service.getContext();
        }
        if (context == null) return null;
        return context.getSharedPreferences(SHARED_PREFERENCES_DEFAULT, Context.MODE_PRIVATE);
    }

    public static void clear() {
        getKeyValuePrefs().edit().clear().apply();
    }

    /**
     * Сохранение строки
     */
    public static void saveString(String key, String value) {
        if (getKeyValuePrefs() == null) return;
        SharedPreferences.Editor editor = getKeyValuePrefs().edit();
        editor.putString(key, value);
        editor.apply();
    }


    /**
     * Получение строки
     */
    public static String getString(String key) {
        if (getKeyValuePrefs() == null) return null;
        return getKeyValuePrefs().getString(key, null);
    }

    /**
     * Получение строки
     */
    public static void removeString(String key) {
        if (getKeyValuePrefs() == null) return;
        SharedPreferences.Editor editor = getKeyValuePrefs().edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * Получение строки
     */
    public static String getString(String key, String def) {
        if (getKeyValuePrefs() == null) return null;
        return getKeyValuePrefs().getString(key, def);
    }

    /**
     * Сохранение json объекта
     */
    public static void saveObject(String key, Object value) {
        if (getKeyValuePrefs() == null) return;
        try {
            SharedPreferences.Editor editor = getKeyValuePrefs().edit();
            editor.putString(key, JsonParser.getJson(value));
            editor.apply();
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
    }

    /**
     * Получение json объекта
     */
    public static <T> T getObject(String key, Class<T> type) {
        if (getKeyValuePrefs() == null) return null;
        String jsonString = getKeyValuePrefs().getString(key, null);
        if (jsonString != null) {
            return JsonParser.fromJson(jsonString, type);
        }
        return null;
    }


    /**
     * Сохранение json объекта
     */
    public static void saveMap(String key, HashMap value) {
        SharedPreferences.Editor editor = getKeyValuePrefs().edit();
        try {
            editor.putString(key, JsonParser.getJson(value));
            editor.apply();
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
    }

}
