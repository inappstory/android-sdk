package com.inappstory.sdk.stories.utils;


public class SessionManager {
    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }


    private static SessionManager INSTANCE;

}
