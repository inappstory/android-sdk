package com.inappstory.sdk.stories.outercallbacks.common.objects;

public class InAppMessageReaderLaunchData implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "iamReaderLaunchData";

    public InAppMessageReaderLaunchData(
            String readerUniqueId
    ) {
        this.readerUniqueId = readerUniqueId;
    }

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }


    public String getReaderUniqueId() {
        return readerUniqueId;
    }

    private final String readerUniqueId;
}
