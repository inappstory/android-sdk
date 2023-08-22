package com.inappstory.sdk.game.cache;

public class GameNameHolder {
    protected String getGameName(String url) {
        String[] parts = url.split("/");
        String fName = parts[parts.length - 1].split("\\.")[0];
        String[] nameParts = fName.split("_");
        if (nameParts.length > 0) return nameParts[0];
        return "";
    }
}
