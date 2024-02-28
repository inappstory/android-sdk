package com.inappstory.sdk.inputdialog.ui;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.uidomain.InputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.utils.SimpleTextWatcher;

public final class InputDialogMailField extends AppCompatEditText
        implements IInputDialogTextField {

    IInputBaseDialogDataHolder dataHolder = new InputBaseDialogDataHolder();

    public InputDialogMailField(Context context) {
        super(context);
        init();
    }

    public InputDialogMailField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputDialogMailField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackground(null);
        setPaddingRelative(0, 0, 0, 0);
        setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        setSingleLine(false);
        setMaxLines(3);
        setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);

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
    public String getValue() {
        return dataHolder.currentText();
    }

    @Override
    public IInputBaseDialogDataHolder getDataHolder() {
        return dataHolder;
    }
}
