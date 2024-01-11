package com.inappstory.sdk.core.models;

import com.inappstory.sdk.core.models.js.dialogstructure.DialogStructure;

public class DialogData {
    public DialogData(DialogStructure dialogStructure, String widgetId, int storyId) {
        this.dialogStructure = dialogStructure;
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
    private String widgetId;
    private int storyId;
}
