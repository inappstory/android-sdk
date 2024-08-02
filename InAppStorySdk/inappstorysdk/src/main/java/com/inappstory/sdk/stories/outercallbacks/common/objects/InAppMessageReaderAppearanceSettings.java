package com.inappstory.sdk.stories.outercallbacks.common.objects;

public class InAppMessageReaderAppearanceSettings implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "iamReaderAppearanceSettings";

    public InAppMessageReaderAppearanceSettings(
    ) {
    }


    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}
