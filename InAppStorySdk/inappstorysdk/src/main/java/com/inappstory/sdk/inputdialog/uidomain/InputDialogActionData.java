package com.inappstory.sdk.inputdialog.uidomain;

public class InputDialogActionData {
    private InputDialogActionType actionType;

    public InputDialogActionType actionType() {
        return actionType;
    }

    public String data() {
        return data;
    }

    public String widgetId() {
        return widgetId;
    }

    private String data;
    private String widgetId;

    public InputDialogActionData(String data, String widgetId) {
        this.data = data;
        this.widgetId = widgetId;
        this.actionType = InputDialogActionType.SEND;
    }

    public InputDialogActionData(String widgetId) {
        this.widgetId = widgetId;
        this.actionType = InputDialogActionType.CANCEL;
    }
}
