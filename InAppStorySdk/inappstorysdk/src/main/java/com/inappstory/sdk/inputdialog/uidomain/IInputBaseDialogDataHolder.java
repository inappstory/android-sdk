package com.inappstory.sdk.inputdialog.uidomain;


public interface IInputBaseDialogDataHolder {
    String hint();
    String currentText();
    void setHint(String hint);
    void setText(String text);
}
