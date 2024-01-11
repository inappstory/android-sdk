package com.inappstory.sdk.inputdialog.ui;

import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;

public interface IInputDialogTextField {
    int maskLength();
    void setHint(String hint);
    void setTextColor(int color);
    String getValue();
    IInputBaseDialogDataHolder getDataHolder();
}
