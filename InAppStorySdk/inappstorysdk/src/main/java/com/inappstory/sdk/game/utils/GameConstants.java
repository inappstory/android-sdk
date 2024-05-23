package com.inappstory.sdk.game.utils;

import java.util.HashMap;
import java.util.Map;

public class GameConstants {
    public static final String SPLASH_STATIC = "staticPortrait";
    public static final String SPLASH_ANIM = "anim";
    public static final String SPLASH_STATIC_KV = "gameInstanceSplash_";
    public static final String SPLASH_ANIM_KV = "gameInstanceAnimSplash_";

    public static final String INDEX_NAME = "index.html";
    public static final String FILE = "file://";

    public static Map<String, String> getSplashesKeys(boolean useAnim) {
        final Map<String, String> splashesKeys = new HashMap<>();
        splashesKeys.put(SPLASH_STATIC, SPLASH_STATIC_KV);
        if (useAnim) {
            splashesKeys.put(SPLASH_ANIM, SPLASH_ANIM_KV);
        }
        return splashesKeys;
    }
}
