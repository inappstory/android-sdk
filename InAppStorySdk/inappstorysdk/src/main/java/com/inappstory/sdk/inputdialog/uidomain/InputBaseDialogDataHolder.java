package com.inappstory.sdk.inputdialog.uidomain;


public class InputBaseDialogDataHolder implements IInputBaseDialogDataHolder {
    String currentText;
    String hint;

    @Override
    public String hint() {
        return hint;
    }

    @Override
    public String currentText() {
        return currentText;
    }

    @Override
    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public void setText(String text) {
        this.currentText = text;
    }
}
