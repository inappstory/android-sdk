package com.inappstory.sdk.inputdialog.ui;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.uidomain.InputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.utils.SimpleTextWatcher;

public final class InputDialogPlainTextField extends AppCompatEditText
        implements IInputDialogTextField {

    IInputBaseDialogDataHolder dataHolder = new InputBaseDialogDataHolder();

    private int limit;
    private int maxLines;

    public InputDialogPlainTextField(Context context, int limit, int maxLines) {
        super(context);
        this.limit = limit;
        this.maxLines = maxLines;
        init();
    }


    private void init() {
        setBackground(null);
        setPaddingRelative(0, 0, 0, 0);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        setGravity(Gravity.CENTER);
        setSingleLine(false);
        setMaxLines(maxLines);
        addTextWatchers();
    }

    private void addTextWatchers() {
        addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void textChanged(String newText) {
                dataHolder.setText(newText);
            }
        });
    }

    @Override
    public int maskLength() {
        return 0;
    }

    @Override
    public void setHint(String hint) {
        dataHolder.setHint(hint);
        super.setHint(hint);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    @Override
    public void setTextSize(int type, int size) {
        super.setTextSize(type, size);
    }


    @Override
    public void addTextWatcher(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
    }

    @Override
    public void addResetWatcher(TextWatcher watcher) {
        addTextWatcher(watcher);
    }

    @Override
    public void removeTextWatcher(TextWatcher watcher) {
        super.removeTextChangedListener(watcher);
    }


    @Override
    public String getValue() {
        return dataHolder.currentText();
    }

    @Override
    public IInputBaseDialogDataHolder getDataHolder() {
        return dataHolder;
    }
}
