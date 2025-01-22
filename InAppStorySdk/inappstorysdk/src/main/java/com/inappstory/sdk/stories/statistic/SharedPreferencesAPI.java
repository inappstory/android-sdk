package com.inappstory.sdk.stories.statistic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.inappstory.sdk.core.IASCore;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesAPI {
    private final IASCore core;

    public SharedPreferencesAPI(IASCore core) {
        this.core = core;
    }

    private static final String SHARED_PREFERENCES_DEFAULT = "default_n";


    private SharedPreferences getDefaultPreferences() {
        return core.appContext().getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_DEFAULT, Context.MODE_PRIVATE);
    }

    /**
     * Сохранение строки
     */
    private final Object sharedPrefLock = new Object();


    public void saveString(final String key, final String value) {
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
    public String getString(String key) {
        synchronized (sharedPrefLock) {
            SharedPreferences preferences = getDefaultPreferences();
            if (preferences == null) return null;
            return preferences.getString(key, null);
        }
    }

    /**
     * Получение строки
     */
    public void removeString(String key) {
        synchronized (sharedPrefLock) {
            SharedPreferences.Editor editor = getDefaultPreferences().edit();
            editor.remove(key);
            editor.apply();
        }

    }

    /**
     * Получение строки
     */
    public String getString(String key, String def) {
        synchronized (sharedPrefLock) {
            SharedPreferences preferences = getDefaultPreferences();
            if (preferences == null) return null;
            return preferences.getString(key, def);
        }
    }

    /**
     * Сохранение массива строк
     */
    public void saveStringSet(final String key, final Set<String> value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (sharedPrefLock) {
                    SharedPreferences.Editor editor = getDefaultPreferences().edit();
                    editor.putStringSet(key, value);
                    boolean isCommitted = editor.commit();
                    Log.e("saveStringSet", isCommitted + "");
                }
            }
        }).start();

    }


    /**
     * Получение массива строк
     */
    public Set<String> getStringSet(String key) {
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
    public void remove(final String key) {
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
    public void clear() {
        synchronized (sharedPrefLock) {
            getDefaultPreferences().edit().clear().apply();
        }
    }
}