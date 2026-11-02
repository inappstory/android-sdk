package com.inappstory.sdk.parse.to;


import androidx.annotation.NonNull;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public final class ToStringList extends ToModelParser<List<String>> {
    public ToStringList(JSONArray value) {
        super(value);
    }

    public ToStringList(String value) {
        super(value);
    }

    @Override
    protected List<String> parseString(@NonNull String value) {
        try {
            return parseList(new JSONArray(value));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected List<String> parseList(JSONArray jsonArray) {
        try {
            List<String> values = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                values.add(jsonArray.getString(i));
            }
            return values;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    protected String getModelClassName() {
        return "List of String";
    }
}
