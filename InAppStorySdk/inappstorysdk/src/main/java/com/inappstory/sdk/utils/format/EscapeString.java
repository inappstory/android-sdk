package com.inappstory.sdk.utils.format;

import java.util.ArrayList;
import java.util.List;

public class EscapeString {
    public String escape(String raw) {
        List<Integer> indexes = suspectedIndexes(raw);
        List<Integer> insertIndexes = new ArrayList<>();
        for (int index : indexes) {
            int curInd = index-1;
            int slashCount = 0;
            while (true) {
                if (curInd < 0)
                    break;
                if (raw.charAt(curInd) == '\\')
                    slashCount++;
                else
                    break;
                curInd--;
            }
            if (slashCount % 2 == 0) {
                insertIndexes.add(index);
            }
        }
        StringBuilder builder = new StringBuilder(raw);
        for (int i = insertIndexes.size() - 1; i>=0; i--) {
            builder.insert(insertIndexes.get(i), "\\");
        }
        return builder.toString();
    }

    private final char[] symbols = {
            '"', '\n', '\r'
    };

    private List<Integer> suspectedIndexes(String raw) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < raw.length(); i++) {
            for (char symbol : symbols) {
                if (symbol == raw.charAt(i)) {
                    indexes.add(i);
                    break;
                }
            }
        }
        return indexes;
    }
}
