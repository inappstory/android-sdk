package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.stories.outercallbacks.common.objects.SerializableWithKey;


public class DialogStructure implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "dialogStructure";
    public SizeStructure size;
    public ConfigV2 configV2;

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}