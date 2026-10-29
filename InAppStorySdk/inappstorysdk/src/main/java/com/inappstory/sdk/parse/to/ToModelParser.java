package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class ToModelParser<T> {
    protected final String strValue;
    protected final JSONObject jsonValue;
    protected final JSONArray jsonArrayValue;

    public ToModelParser(String strValue) {
        this.strValue = strValue;
        this.jsonValue = null;
        this.jsonArrayValue = null;
    }

    public ToModelParser(JSONObject jsonValue) {
        this.strValue = null;
        this.jsonValue = jsonValue;
        this.jsonArrayValue = null;
    }

    public ToModelParser(JSONArray jsonArrayValue) {
        this.strValue = null;
        this.jsonValue = null;
        this.jsonArrayValue = jsonArrayValue;
    }

    public final T parse() {
        if (strValue != null) {
            return parseString(strValue);
        } else if (jsonValue != null) {
            return parseObject(jsonValue);
        } else if (jsonArrayValue != null) {
            return parseList(jsonArrayValue);
        } else {
            return null;
        }
    }

    protected T parseList(JSONArray jsonArray) {
        return null;
    }

    protected T parseObject(JSONObject jsonObject) {
        return null;
    }

    protected T parseString(@NonNull String value) {
        return null;
    }

    public String parseError(String field) {
        return "Field " + field + " has incorrect format.";
    }

    protected abstract String getClassName();

    public String parseError() {
        return "Can't parse model: " + getClassName();
    }
}
