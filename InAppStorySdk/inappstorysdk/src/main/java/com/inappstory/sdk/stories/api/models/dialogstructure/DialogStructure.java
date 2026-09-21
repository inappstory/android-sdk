package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.core.inputdialog.InputDialogData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SerializableWithKey;


public class DialogStructure implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "dialogStructure";
    public SizeStructure size;
    public ConfigV2 configV2;

    public InputDialogData toInputDialogData() {
        try {
            return new InputDialogData(
                    configV2.main.question.text.value,
                    configV2.main.input.text.placeholder,
                    configV2.main.input.type,
                    configV2.main.button.text.value
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}