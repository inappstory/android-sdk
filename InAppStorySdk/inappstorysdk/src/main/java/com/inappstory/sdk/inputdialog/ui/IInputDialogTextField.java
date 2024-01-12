package com.inappstory.sdk.inputdialog.ui;

import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;

public interface IInputDialogTextField {
    int maskLength();
    void setHint(String hint);
    void setTextColor(int color);
    void setHintTextColor(int color);
    String getValue();
    IInputBaseDialogDataHolder getDataHolder();
}
