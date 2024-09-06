package com.inappstory.sdk.core.ui.screens;

public class ScreensManager {

    private ScreensManager() {

    }

    private final static ScreensManager INSTANCE = new ScreensManager();

    public static ScreensManager getInstance() {
        return INSTANCE;
    }



}
