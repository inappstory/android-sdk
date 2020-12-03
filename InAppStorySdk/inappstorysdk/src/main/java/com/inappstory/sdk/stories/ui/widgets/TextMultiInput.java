package com.inappstory.sdk.stories.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Size;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.inappstory.sdk.stories.utils.PhoneFormats;
import com.inappstory.sdk.stories.utils.Sizes;

public class TextMultiInput extends LinearLayout {
    public TextMultiInput(Context context) {
        super(context);
    }


    public void setHint(String hint) {
        getMainText().setHint(hint);
    }

    public void setTextColor(int textColor) {
        getMainText().setTextColor(textColor);
        if (inputType == PHONE)
            getCountryCodeText().setTextColor(textColor);
    }


    public String getText() {
        if (inputType == PHONE) {
            return getCountryCodeText().getEditableText().toString() +
                    getMainText().getEditableText().toString();
        } else {
            return getMainText().getEditableText().toString();
        }
    }

    public void setHintTextColor(int hintColor) {
        getMainText().setHintTextColor(hintColor);
    }

    public void setTextSize(int size) {
        if (inputType == PHONE)
            getCountryCodeText().setTextSize(size);
        getMainText().setTextSize(size);
    }

    public static final String PHONE_CODE_MASK = "+____";

    public AppCompatEditText getMainText() {
        return mainText;
    }

    AppCompatEditText mainText;

    public AppCompatEditText getCountryCodeText() {
        return countryCodeText;
    }

    AppCompatEditText countryCodeText;

    public static final int PHONE = 0;
    public static final int MAIL = 1;
    public static final int TEXT = 2;

    public int inputType;

    public void init(int inputType) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        this.inputType = inputType;
        mainText = new AppCompatEditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        lp.setMargins(0, 0, 0, 0);
        mainText.setBackground(null);
        mainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        switch (inputType) {
            case MAIL:
                mainText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case TEXT:
                mainText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;
            case PHONE:
                lp.setMargins(Sizes.dpToPxExt(4), 0, 0, 0);
                countryCodeText = new AppCompatEditText(getContext());
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(Sizes.dpToPxExt(60),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.setMargins(0, 0, Sizes.dpToPxExt(4), 0);
                divider = new View(getContext());
                divider.setLayoutParams(new ViewGroup.LayoutParams(Sizes.dpToPxExt(1),
                        Sizes.dpToPxExt(30)));
                countryCodeText.setLayoutParams(lp2);
                countryCodeText.setBackground(null);
                countryCodeText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                countryCodeText.setInputType(InputType.TYPE_CLASS_PHONE);
                countryCodeText.setGravity(Gravity.CENTER);
                countryCodeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                countryCodeText.addTextChangedListener(new MaskedWatcher(PHONE_CODE_MASK, "+"));
                countryCodeText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String mask = PhoneFormats.getMaskByCode(s.toString());
                        try {
                            mainText.removeTextChangedListener(watcher);
                        } catch (Exception e) {

                        }
                        if (mask != null) {
                            watcher = new MaskedWatcher(mask, "");
                            mainText.addTextChangedListener(watcher);
                            mainText.setHint(mask);
                        } else {
                            mainText.setHint("");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                mainText.setInputType(InputType.TYPE_CLASS_PHONE);
                addView(countryCodeText);
                addView(divider);
                break;
        }
        mainText.setLayoutParams(lp);
        addView(mainText);
    }

    MaskedWatcher watcher;

    public View getDivider() {
        return divider;
    }

    View divider;

    public TextMultiInput(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextMultiInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
