package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.graphics.Color;

import androidx.core.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class ColorParser {
    public static Map<String, Pair<String, String>> colorsMap;

    static {
        colorsMap = new HashMap<>();
        colorsMap.put("black", new Pair<>("#000000", "#c1c1c1"));
        colorsMap.put("white", new Pair<>("#ffffff", "#ffc800"));
        colorsMap.put("red", new Pair<>("#ff1a36", "#eb5c57"));
        colorsMap.put("yellow", new Pair<>("#fff000", "#fbda61"));
        colorsMap.put("green", new Pair<>("#0be361", "#b4ed50"));
        colorsMap.put("violet", new Pair<>("#b300bc", "#0164fd"));
        colorsMap.put("blue", new Pair<>("#0761db", "#b4ed50"));
        colorsMap.put("grey", new Pair<>("#a8a8a8", "#e2e2e2"));
    }
    static Pair<String, String> transparent = new Pair<>("#00000000", "#00000000");


    public static int getColor(String color, boolean secondary) {
        if (color == null || color.isEmpty()) return Color.TRANSPARENT;
        String[] vals;
        try {
            if (color.charAt(0) == '#') {
                return Color.parseColor(color);
            } else if (colorsMap.containsKey(color)) {
                return Color.parseColor(secondary ?
                        colorsMap.get(color).second :
                        colorsMap.get(color).first);
            } else if (color.startsWith("rgba")) {
                color = color.replaceAll("\\s", "");
                color = color.replaceAll("\\(", "");
                color = color.replaceAll("\\)", "");
                color = color.replaceAll("rgba", "");
                vals = color.split(",");
                return rgbaToColor(Integer.parseInt(vals[0]),
                        Integer.parseInt(vals[1]),
                        Integer.parseInt(vals[2]),
                        Float.parseFloat(vals[3]));
            } else if (color.startsWith("rgb")) {
                color = color.replaceAll("\\s", "");
                color = color.replaceAll("\\(", "");
                color = color.replaceAll("\\)", "");
                color = color.replaceAll("rgb", "");
                vals = color.split(",");
                return rgbaToColor(Integer.parseInt(vals[0]),
                        Integer.parseInt(vals[1]),
                        Integer.parseInt(vals[2]),
                        1f);
            } else
                return Color.TRANSPARENT;
        } catch (Exception e) {
            return Color.TRANSPARENT;
        }
    }

    public static Pair<String, String> getGradientColor(String color) {
        if (color == null || color.isEmpty()) return transparent;
        if (colorsMap.containsKey(color))
            return colorsMap.get(color);
        return transparent;
    }

    static int rgbaToColor(int red, int green, int blue, Float alpha) {
        return Color.argb((int) (alpha * 255), red, green, blue);
    }
}
