package com.inappstory.sdk.utils;

import android.text.TextUtils;

import java.util.List;

public class MDStringModel {
    public final String rawText;
    public final String keyText;
    public final String valueText;
    public final List<MDStringReplacement> replacements;

    public MDStringModel(String rawText, String keyText, String valueText, List<MDStringReplacement> replacements) {
        this.rawText = rawText;
        this.keyText = keyText;
        this.valueText = valueText;
        this.replacements = replacements;
    }

    @Override
    public String toString() {
        return "MDStringModel{" +
                "rawString='" + rawText + '\'' +
                ", replacedString='" + keyText + '\'' +
                ", replacements=[" + TextUtils.join(",", replacements) +
                "]}";
    }
}
