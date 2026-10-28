package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public abstract class ToListParser<T, M extends ToModelParser<T>> {
    protected final String strValue;
    protected final JSONArray jsonArrayValue;

    protected abstract M createParserInstance(JSONObject object);

    public ToListParser(String strValue) {
        this.strValue = strValue;
        this.jsonArrayValue = null;
    }

    public ToListParser(JSONArray jsonArrayValue) {
        this.strValue = null;
        this.jsonArrayValue = jsonArrayValue;
    }

    public final List<T> parse() {
        if (strValue != null) {
            return parseString(strValue);
        } else if (jsonArrayValue != null) {
            return parseJSONArray(jsonArrayValue);
        } else {
            return null;
        }
    }

    private List<T> parseJSONArray(JSONArray jsonArray) {
        if (jsonArray == null) return null;
        try {
            List<T> values = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                values.add(
                        createParserInstance(jsonArray.getJSONObject(i))
                                .parse()
                );
            }
            return values;
        } catch (Exception e) {
            return null;
        }
    }

    private List<T> parseString(@NonNull String value) {
        try {
            JSONArray jsonArray = new JSONArray(value);
            List<T> values = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                values.add(
                        createParserInstance(jsonArray.getJSONObject(i))
                                .parse()
                );
            }
            return values;
        } catch (Exception e) {
            return null;
        }
    }

    protected String parseError(String field) {
        return "Field " + field + " has incorrect format.";
    }
}
