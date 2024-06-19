package com.inappstory.sdk.packages.core.base.network.utils;

import android.util.Pair;

import com.inappstory.sdk.network.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class ObjectToQuery {
    public List<Pair<String, String>> convert(String mainName, String object) {
        try {
            return JsonParser.toQueryParams(mainName, object);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
