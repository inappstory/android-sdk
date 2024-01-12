package com.inappstory.sdk.inputdialog.ui;

import android.graphics.Typeface;

import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;

public interface IInputDialogTextField {
    int maskLength();
    void setHint(String hint);
    void setTextColor(int color);
    void setHintTextColor(int color);
    void setTextSize(int type, int size);
    void setTypeface(Typeface typeface, int style);
    Typeface getTypeface();
    String getValue();
    IInputBaseDialogDataHolder getDataHolder();
}
