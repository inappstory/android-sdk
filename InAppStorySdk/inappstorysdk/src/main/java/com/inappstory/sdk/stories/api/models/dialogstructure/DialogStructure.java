package com.inappstory.sdk.stories.api.models.dialogstructure;

import com.inappstory.sdk.core.inputdialog.InputDialogData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SerializableWithKey;


public class DialogStructure implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "dialogStructure";
    public SizeStructure size;
    public ConfigV2 configV2;

    public InputDialogData toInputDialogData() {
        InputDialogData inputDialogData = new InputDialogData();
        try {
            inputDialogData.title = configV2.main.question.text.value;
            inputDialogData.submitButton = configV2.main.button.text.value;
            inputDialogData.hint = configV2.main.input.text.placeholder;
            inputDialogData.type = configV2.main.input.type;
            return inputDialogData;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}