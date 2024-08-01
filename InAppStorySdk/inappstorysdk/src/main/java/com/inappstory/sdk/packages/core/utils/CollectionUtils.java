package com.inappstory.sdk.packages.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionUtils {
    public static <T> List<T> mapOfArraysToArrayList(Map<String, List<T>> map) {
        List<T> arrayList = new ArrayList<>();
        if (map != null)
            for (List<T> value : map.values()) {
                arrayList.addAll(value);
            }
        return arrayList;
    }
}
