package com.inappstory.sdk.inputdialog.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.inappstory.sdk.core.models.js.dialogstructure.InputStructure;
import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.uidomain.InputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.utils.SimpleTextWatcher;
import com.inappstory.sdk.utils.FontUtils;

public final class InputDialogMailField extends AppCompatEditText
        implements IInputDialogTextField {

    IInputBaseDialogDataHolder dataHolder = new InputBaseDialogDataHolder();
    private InputStructure inputStructure;
    private float factor = 1f;

    public InputDialogMailField(
            Context context,
            InputStructure inputStructure,
            float factor
    ) {
        super(context);
        this.factor = factor;
        this.inputStructure = inputStructure;
        init();
    }

    private void init() {

        setBackground(null);
        setPaddingRelative(0, 0, 0, 0);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        setSingleLine(true);
        setMaxLines(1);


        FontUtils.setTypeface(this,
                inputStructure.text().isBold(),
                inputStructure.text().isItalic(),
                inputStructure.text().isSecondary()
        );

        setHint(inputStructure.text().placeholder());
        setTextColor(Color.parseColor(inputStructure.text().color()));
        setHintTextColor(Color.parseColor(inputStructure.text().color()));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (factor * inputStructure.text().size()));

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
