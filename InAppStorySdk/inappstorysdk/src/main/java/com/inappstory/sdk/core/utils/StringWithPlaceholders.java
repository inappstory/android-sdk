package com.inappstory.sdk.core.utils;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;

import java.util.Map;

public class StringWithPlaceholders {
    public String replace(String input, IASCore core) {
        Map<String, String> localPlaceholders =
                ((IASDataSettingsHolder) core.settingsAPI()).placeholders();
        String tmp = input;
        for (String key : localPlaceholders.keySet()) {
            String modifiedKey = "%" + key + "%";
            String value = localPlaceholders.get(key);
            if (value != null) {
                tmp = tmp.replace(modifiedKey, value);
            }
        }
        return tmp;
    }
}
