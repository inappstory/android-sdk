package com.inappstory.sdk.stories.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.network.JsonParser;

import java.util.HashMap;

public class KeyValueStorage {

    private final IASCore core;

    public KeyValueStorage(IASCore core) {
        this.core = core;
    }


    private Context context;

    private static final String SHARED_PREFERENCES_DEFAULT = "key_value_prefs";

    private SharedPreferences getKeyValuePrefs() {
        return core.appContext().getSharedPreferences(SHARED_PREFERENCES_DEFAULT, Context.MODE_PRIVATE);
    }

    public void clear() {
        getKeyValuePrefs().edit().clear().apply();
    }

    /**
     * Сохранение строки
     */
    public void saveString(String key, String value) {
        if (getKeyValuePrefs() == null) return;
        SharedPreferences.Editor editor = getKeyValuePrefs().edit();
        editor.putString(key, value);
        editor.apply();
    }


    /**
     * Получение строки
     */
    public String getString(String key) {
        if (getKeyValuePrefs() == null) return null;
        return getKeyValuePrefs().getString(key, null);
    }

    /**
     * Получение строки
     */
    public void removeString(String key) {
        if (getKeyValuePrefs() == null) return;
        SharedPreferences.Editor editor = getKeyValuePrefs().edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * Получение строки
     */
    public String getString(String key, String def) {
        if (getKeyValuePrefs() == null) return null;
        return getKeyValuePrefs().getString(key, def);
    }

    /**
     * Сохранение json объекта
     */
    public void saveObject(String key, Object value) {
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
    public <T> T getObject(String key, Class<T> type) {
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
    public void saveMap(String key, HashMap value) {
        SharedPreferences.Editor editor = getKeyValuePrefs().edit();
        try {
            editor.putString(key, JsonParser.getJson(value));
            editor.apply();
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
    }

}
