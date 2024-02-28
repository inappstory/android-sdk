package com.inappstory.sdk.utils;

import android.graphics.Typeface;
import android.widget.TextView;


import com.inappstory.sdk.AppearanceManager;

public class FontUtils {
    public static void setTypeface(
            TextView textView,
            boolean bold,
            boolean italic,
            boolean secondary
    ) {
        Typeface t = AppearanceManager.getCommonInstance().getFont(secondary, bold, italic);
        int boldV = bold ? 1 : 0;
        int italicV = italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), boldV + italicV);
    }
}
