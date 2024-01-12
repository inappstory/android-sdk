package com.inappstory.sdk.inputdialog.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.inputdialog.uidomain.IInputBaseDialogDataHolder;
import com.inappstory.sdk.inputdialog.uidomain.IInputPhoneDialogDataHolder;
import com.inappstory.sdk.inputdialog.uidomain.InputPhoneDialogDataHolder;
import com.inappstory.sdk.inputdialog.utils.SimpleTextWatcher;
import com.inappstory.sdk.stories.ui.widgets.MaskedWatcher;
import com.inappstory.sdk.stories.utils.PhoneFormats;
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
        mainText = new AppCompatEditText(getContext());
        mainText.setPaddingRelative(0, 0, 0, 0);
        mainText.setLayoutParams(layoutParams);
        mainText.setBackground(null);
        mainText.setInputType(InputType.TYPE_CLASS_PHONE);
        mainText.setSingleLine(true);
        mainText.setMaxLines(1);
    }

    private void createCountryCodeTextField(LayoutParams layoutParams) {
        countryCodeText = new AppCompatEditText(getContext());
        countryCodeText.setPaddingRelative(0, 0, 0, 0);
        countryCodeText.setBackground(null);
        countryCodeText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        countryCodeText.setInputType(InputType.TYPE_CLASS_PHONE);
        countryCodeText.setGravity(Gravity.CENTER);
        countryCodeText.setLayoutParams(layoutParams);

        countryCodeText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view1, boolean b) {
                if (!b) return;
                if (view1 instanceof AppCompatEditText) {
                    final AppCompatEditText countryCodeText = (AppCompatEditText) view1;
                    Editable text = countryCodeText.getText();
                    if (text != null && text.length() < 1) {
                        countryCodeText.setText("+");
                        countryCodeText.post(new Runnable() {
                            @Override
                            public void run() {
                                countryCodeText.setSelection(1);
                            }
                        });
                    }
                }
            }
        });
    }

    private void createDivider() {
        divider = new View(getContext());
        divider.setLayoutParams(new ViewGroup.LayoutParams(Sizes.dpToPxExt(1, getContext()),
                Sizes.dpToPxExt(30, getContext())));
    }

    private void createPhoneNumberHintField(LayoutParams layoutParams) {
        phoneNumberHint = new AppCompatEditText(getContext());
        phoneNumberHint.setPaddingRelative(0, 0, 0, 0);
        phoneNumberHint.setBackground(null);
        phoneNumberHint.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneNumberHint.setClickable(false);
        phoneNumberHint.setFocusable(false);
        phoneNumberHint.setLayoutParams(layoutParams);
        phoneNumberHint.setSingleLine(true);
        phoneNumberHint.setMaxLines(1);
    }

    private void init() {

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        LayoutParams mainTextLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        mainTextLp.setMargins(Sizes.dpToPxExt(4, getContext()), 0, 0, 0);

        int countryCodeWidth = Sizes.dpToPxExt(60, getContext());
        LayoutParams countryCodeLp = new LayoutParams(countryCodeWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        countryCodeLp.setMargins(0, 0, 0, 0);

        createMainTextField(mainTextLp);
        createDivider();
        createCountryCodeTextField(countryCodeLp);
        createPhoneNumberHintField(mainTextLp);

        RelativeLayout rl = new RelativeLayout(getContext());
        rl.setLayoutParams(mainTextLp);


        rl.addView(phoneNumberHint);
        rl.addView(mainText);
        addView(countryCodeText);
        addView(divider);
        addView(rl);

        addTextWatchers();
    }


    MaskedWatcher watcher;

    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            boolean keepOriginal = true;
            StringBuilder sb = new StringBuilder(end - start);
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (isCharAllowed(c)) // put your condition here
                    sb.append(c);
                else
                    keepOriginal = false;
            }
            if (keepOriginal)
                return null;
            else {
                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(sb);
                    TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                    return sp;
                } else {
                    return sb;
                }
            }
        }

        private boolean isCharAllowed(char c) {
            return Character.isDigit(c);
        }
    };

    Observer<String> maskObserver = new Observer<String>() {
        @Override
        public void onChanged(String mask) {
            try {
                watcher.active = false;
                mainText.removeTextChangedListener(watcher);
                watcher = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mask != null) {
                mainText.setFilters(new InputFilter[]{});
                mainText.setHint("");
                String text = mainText.getText().toString();
                mainText.setText("");
                watcher = new MaskedWatcher(mask, "");
                mainText.addTextChangedListener(watcher);
                phoneNumberHint.setHint(mask);

                text = text.replaceAll(" ", "");
                mainText.setText(text);
                watcher.afterTextChanged(mainText.getEditableText());

                mainText.setInputType(InputType.TYPE_CLASS_PHONE);
            } else {
                mainText.setFilters(new InputFilter[]{filter});
                String text = mainText.getText().toString();
                text = text.replaceAll(" ", "");
                mainText.setText(text);
                if (countryCodeText.getText().length() == 1) {
                    mainText.setHint(dataHolder.hint());
                } else {
                    mainText.setHint("");
                }

                mainText.setInputType(InputType.TYPE_CLASS_PHONE);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        dataHolder.mask().observeForever(maskObserver);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dataHolder.mask().removeObserver(maskObserver);
    }

    private void addTextWatchers() {
        countryCodeText.addTextChangedListener(new MaskedWatcher(PHONE_CODE_MASK, "+"));
        countryCodeText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void textChanged(String newText) {

                String mask = PhoneFormats.getMaskByCode(newText);
                dataHolder.setMask(mask);
            }

        });

        mainText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void textChanged(String newText) {
                String mask = dataHolder.mask().getValue();
                if (mask != null) {
                    if (newText.length() <= mask.length()) {
                        String s0 = newText + mask.substring(newText.length());
                        phoneNumberHint.setHint(s0);
                    }
                } else {
                    if (newText.length() < 20) {
                        phoneNumberHint.setHint(newText);
                    } else {
                        phoneNumberHint.setHint("");
                    }
                }
            }
        });
    }

    @Override
    public int maskLength() {
        String mask = dataHolder.mask().getValue();
        return mask != null ? mask.length() : 0;
    }

    @Override
    public void setHint(String hint) {
        dataHolder.setHint(hint);
        phoneNumberHint.setHint(hint);
    }

    @Override
    public void setTextColor(int color) {
        mainText.setTextColor(color);
        countryCodeText.setTextColor(color);
    }

    @Override
    public void setHintTextColor(int color) {
        mainText.setHintTextColor(color);
        phoneNumberHint.setHintTextColor(color);
    }

    @Override
    public void setTextSize(int type, int size) {
        countryCodeText.setTextSize(type, size);
        phoneNumberHint.setTextSize(type, size);
        mainText.setTextSize(type, size);
    }

    @Override
    public void setTypeface(Typeface typeface, int style) {
        countryCodeText.setTypeface(typeface, style);
        phoneNumberHint.setTypeface(typeface, style);
        mainText.setTypeface(typeface, style);
    }

    @Override
    public Typeface getTypeface() {
        return mainText.getTypeface();
    }


    @Override
    public String getValue() {
        return countryCodeText.getEditableText().toString() + " " +
                mainText.getEditableText().toString();
    }

    @Override
    public IInputBaseDialogDataHolder getDataHolder() {
        return dataHolder;
    }
}
