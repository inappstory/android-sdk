package com.inappstory.sdk.inputdialog.ui;

import android.graphics.Typeface;
import android.text.TextWatcher;

import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.utils.SimpleTextWatcher;

public interface IInputDialogTextField {
    int maskLength();
    void setHint(String hint);
    void setTextColor(int color);
    void setHintTextColor(int color);
    void setTextSize(int type, int size);
    void addTextWatcher(TextWatcher watcher);
    void addResetWatcher(TextWatcher watcher);
    void removeTextWatcher(TextWatcher watcher);
    void setText(CharSequence text);
    void setSelection(int index);
    int getSelectionStart();
    void setTypeface(Typeface typeface, int style);
    Typeface getTypeface();
    String getValue();
    IInputBaseDialogDataHolder getDataHolder();
}
