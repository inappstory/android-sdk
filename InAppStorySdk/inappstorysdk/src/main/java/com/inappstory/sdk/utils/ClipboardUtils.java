package com.inappstory.sdk.utils;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.WriteClipboardData;

import java.util.Objects;

public class ClipboardUtils {
    public static void writeToClipboard(String payload, Context context) {
        WriteClipboardData data = JsonParser.fromJson(payload, WriteClipboardData.class);
        if (data == null || data.textValue == null) return;
        if (Objects.equals(data.type, "text")) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied", data.textValue);
            clipboard.setPrimaryClip(clip);
        }
    }
}
