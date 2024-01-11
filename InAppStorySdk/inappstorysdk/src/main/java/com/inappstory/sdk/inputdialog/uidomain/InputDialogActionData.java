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

    private InputDialogActionData(
            String data,
            String widgetId,
            InputDialogActionType type
    ) {
        this.data = data;
        this.widgetId = widgetId;
        this.actionType = type;
    }

    public static InputDialogActionData send(String data, String widgetId) {
        return new InputDialogActionData(
                data, widgetId, InputDialogActionType.SEND
        );
    }

    public static InputDialogActionData cancel(String widgetId) {
        return new InputDialogActionData(
                null, widgetId, InputDialogActionType.CANCEL
        );
    }

    public static InputDialogActionData error(String error, String widgetId) {
        return new InputDialogActionData(
                error, widgetId, InputDialogActionType.ERROR
        );
    }
}
