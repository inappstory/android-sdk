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

    public InputDialogPlainTextField(Context context) {
        super(context);
        init();
    }


    public InputDialogPlainTextField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputDialogPlainTextField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackground(null);
        setPaddingRelative(0, 0, 0, 0);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        setGravity(Gravity.CENTER);
        setSingleLine(true);
        setMaxLines(1);
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
    public String getValue() {
        return dataHolder.currentText();
    }

    @Override
    public IInputBaseDialogDataHolder getDataHolder() {
        return dataHolder;
    }
}
