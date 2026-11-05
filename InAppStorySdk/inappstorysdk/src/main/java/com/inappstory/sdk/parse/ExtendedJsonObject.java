package com.inappstory.sdk.parse;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ExtendedJsonObject {
    private final JSONObject value;

    public ExtendedJsonObject(@NonNull JSONObject value) {
        this.value = value;
        cleanJson(this.value);
        Log.e("cleaned", value.toString());
    }

    void cleanJsonArray(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.get(i) instanceof JSONArray) {
                cleanJsonArray((JSONArray) jsonArray.get(i));
            } else if (jsonArray.get(i) instanceof JSONObject) {
                cleanJson((JSONObject) jsonArray.get(i));
            } else if ("null".equals(jsonArray.get(i).toString().trim())) {
                jsonArray.put(i, null);
            }
        }
    }

    void cleanJson(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        List<String> keysList = new ArrayList<>();
        while (keys.hasNext()) {
            keysList.add(keys.next());
        }
        for (String key : keysList) {
            try {
                Object value = jsonObject.get(key);
                if (value instanceof JSONArray) {
                    cleanJsonArray((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    cleanJson((JSONObject) value);
                } else if ("null".equals(value.toString().trim())) {
                    jsonObject.put(key, null);
                    Log.e("cleaned", key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String optStringNullable(String name) {
        try {
            return value.getString(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public Integer optIntNullable(String name) {
        try {
            return value.getInt(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public Boolean optBooleanNullable(String name) {
        try {
            return value.getBoolean(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public Long optLongNullable(String name) {
        try {
            return value.getLong(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public Double optDoubleNullable(String name) {
        try {
            return value.getDouble(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public String optString(String name) {
        return value.optString(name);
    }

    public int optInt(String name) {
        return value.optInt(name);
    }

    public boolean optBoolean(String name) {
        return value.optBoolean(name);
    }

    public boolean optBoolean(String name, boolean defValue) {
        return value.optBoolean(name, defValue);
    }

    public long optLong(String name) {
        return value.optLong(name);
    }

    public double optDouble(String name) {
        return value.optDouble(name);
    }

    public String getString(String name) throws JSONException {
        return value.getString(name);
    }

    public Integer getInt(String name) throws JSONException {
        return value.getInt(name);
    }

    public Boolean getBoolean(String name) throws JSONException {
        return value.getBoolean(name);
    }

    public long getLong(String name) throws JSONException {
        return value.getLong(name);
    }

    public double getDouble(String name) throws JSONException {
        return value.getDouble(name);
    }

    public JSONObject getJSONObject(String name) throws JSONException {
        return value.getJSONObject(name);
    }

    public JSONArray getJSONArray(String name) throws JSONException {
        return value.getJSONArray(name);
    }

    public JSONObject optJSONObject(String name) {
        return value.optJSONObject(name);
    }

    public JSONArray optJSONArray(String name) {
        return value.optJSONArray(name);
    }
}
