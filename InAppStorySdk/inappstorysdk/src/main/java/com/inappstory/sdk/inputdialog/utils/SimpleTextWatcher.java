package com.inappstory.sdk.inputdialog.utils;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {

    @Override
    public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public final void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null)
            textChanged(s.toString());
        else
            textChanged(null);
    }

    @Override
    public final void afterTextChanged(Editable s) {

    }

    public abstract void textChanged(String newText);
}
