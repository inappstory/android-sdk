package com.inappstory.sdk.inputdialog.ui;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.uidomain.IInputPhoneDialogDataHolder;
import com.inappstory.sdk.inputdialog.uidomain.InputPhoneDialogDataHolder;
import com.inappstory.sdk.stories.utils.Sizes;

public final class InputDialogPhoneField extends LinearLayout
        implements IInputDialogTextField {
    IInputPhoneDialogDataHolder dataHolder = new InputPhoneDialogDataHolder();

    AppCompatEditText mainText;
    AppCompatEditText countryCodeText;
    AppCompatEditText phoneNumberHint;
    View divider;

    private final String PHONE_CODE_MASK = "+−−−−";


    public InputDialogPhoneField(Context context) {
        super(context);
        init();
    }

    public InputDialogPhoneField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputDialogPhoneField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void createMainTextField(LayoutParams layoutParams) {

    }

    private void createCountryCodeTextField() {

    }

    private void createDivider() {

    }

    private void createPhoneNumberHintField(LayoutParams layoutParams) {
        phoneNumberHint = new AppCompatEditText(getContext());
        phoneNumberHint.setPaddingRelative(0, 0, 0, 0);
    }

    private void init() {
        divider = new View(getContext());

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams mainTextLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        mainTextLp.setMargins(Sizes.dpToPxExt(4, getContext()), 0, 0, 0);
        mainText = new AppCompatEditText(getContext());
        mainText.setPaddingRelative(0, 0, 0, 0);
        mainText.setLayoutParams(mainTextLp);
        mainText.setBackground(null);
        mainText.setInputType(InputType.TYPE_CLASS_PHONE);
        mainText.setSingleLine(true);
        mainText.setMaxLines(1);


        int countryCodeWidth = Sizes.dpToPxExt(60, getContext());
        LayoutParams countryCodeLp = new LayoutParams(countryCodeWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        countryCodeLp.setMargins(0, 0, 0, 0);
        countryCodeText = new AppCompatEditText(getContext());
        countryCodeText.setPaddingRelative(0, 0, 0, 0);
        countryCodeText.setBackground(null);
        countryCodeText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        countryCodeText.setInputType(InputType.TYPE_CLASS_PHONE);
        countryCodeText.setGravity(Gravity.CENTER);
        countryCodeText.setLayoutParams(countryCodeLp);

        divider.setLayoutParams(new ViewGroup.LayoutParams(Sizes.dpToPxExt(1, getContext()),
                Sizes.dpToPxExt(30, getContext())));
        RelativeLayout rl = new RelativeLayout(getContext());
        rl.setLayoutParams(mainTextLp);

        phoneNumberHint.setBackground(null);
        phoneNumberHint.setInputType(InputType.TYPE_CLASS_PHONE);

        phoneNumberHint.setClickable(false);
        phoneNumberHint.setFocusable(false);
        phoneNumberHint.setLayoutParams(mainTextLp);
        phoneNumberHint.setSingleLine(true);
        phoneNumberHint.setMaxLines(1);

        rl.addView(phoneNumberHint);
        rl.addView(mainText);
        addView(countryCodeText);
        addView(divider);
        addView(rl);


        addTextWatchers();
    }

    private void addTextWatchers() {

    }

    @Override
    public int maskLength() {
        return 0;
    }

    @Override
    public void setHint(String hint) {

    }

    @Override
    public void setTextColor(int color) {

    }


    @Override
    public String getValue() {
        return null;
    }

    @Override
    public IInputBaseDialogDataHolder getDataHolder() {
        return dataHolder;
    }
}
