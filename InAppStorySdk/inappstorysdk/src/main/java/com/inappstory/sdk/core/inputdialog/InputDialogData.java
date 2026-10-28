package com.inappstory.sdk.core.inputdialog;


public class InputDialogData {
    private String title;
    private String hint;
    private String type;
    private String submitButton;
    private String negativeButton;
    private String neutralButton;

    public InputDialogData() {

    }

    public InputDialogData(
            String title,
            String hint,
            String type,
            String submitButton
    ) {
        this.type = type;
        this.title = title;
        this.hint = hint;
        this.submitButton = submitButton;
    }

    public String title() {
        return title;
    }

    public String hint() {
        return hint;
    }

    public InputDialogDataType type() {
        switch (type) {
            case "tel":
                return InputDialogDataType.PHONE;
            case "email":
                return InputDialogDataType.MAIL;
            default:
                return InputDialogDataType.TEXT;
        }
    }

    public String submitButton() {
        return submitButton;
    }
}
