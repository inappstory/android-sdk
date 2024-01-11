package com.inappstory.sdk.core.models;

import com.inappstory.sdk.core.models.js.dialogstructure.DialogStructure;

public class DialogData {
    public DialogData(DialogStructure dialogStructure, String widgetId, int storyId) {
        this.dialogStructure = dialogStructure;
        this.dialogType = DialogType.TEXT;
        try {
            String type = dialogStructure.configV2.main.input.type;
            switch (type) {
                case "email":
                    dialogType = DialogType.MAIL;
                    break;
                case "phone":
                    dialogType = DialogType.PHONE;
                    break;
                default:
                    break;
            }
        } catch (Exception ignored) {}
        this.widgetId = widgetId;
        this.storyId = storyId;
    }

    public DialogStructure dialogStructure() {
        return dialogStructure;
    }

    public String widgetId() {
        return widgetId;
    }

    public int storyId() {
        return storyId;
    }

    private DialogStructure dialogStructure;

    public DialogType dialogType() {
        return dialogType;
    }

    private DialogType dialogType;
    private String widgetId;
    private int storyId;
}
