package com.inappstory.sdk.stories.outercallbacks.common.objects;

public class GameReaderAppearanceSettings implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "gameReaderAppearanceSettings";

    public GameReaderAppearanceSettings(
            String navBarColor,
            String statusBarColor
    ) {
        this.navBarColor = navBarColor;
        this.statusBarColor = statusBarColor;
    }

    public final String navBarColor;
    public final String statusBarColor;

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}
