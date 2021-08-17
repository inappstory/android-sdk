package com.inappstory.sdk.stories.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Size;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.stories.utils.PhoneFormats;
import com.inappstory.sdk.stories.utils.Sizes;

public class TextMultiInput extends LinearLayout {
    public TextMultiInput(Context context) {
        super(context);
    }

    public int getMaskLength() {
        if (watcher != null) {
            return watcher.mMask.length();
        }
        return 0;
    }

    public void setHint(String hint) {
        if (inputType == PHONE) {
            phoneNumberHint.setHint(hint);
        } else {
            getMainText().setHint(hint);
        }
        baseHint = hint;
    }

    private String baseHint = "";

    public void setTextColor(int textColor) {
        getMainText().setTextColor(textColor);
        if (inputType == PHONE) {
            getCountryCodeText().setTextColor(textColor);
        }
    }


    public String getText() {
        if (inputType == PHONE) {
            return getCountryCodeText().getEditableText().toString() + " " +
                    getMainText().getEditableText().toString();
        } else {
            return getMainText().getEditableText().toString();
        }
    }

    public void setHintTextColor(int hintColor) {
        getMainText().setHintTextColor(hintColor);
        if (inputType == PHONE) {
            phoneNumberHint.setHintTextColor(hintColor);
        }

    }

    public void setTextSize(int size) {
        if (inputType == PHONE) {
            getCountryCodeText().setTextSize(size);
            phoneNumberHint.setTextSize(size);
        }
        getMainText().setTextSize(size);
    }

    public static final String PHONE_CODE_MASK = "+−−−−";

    public AppCompatEditText getMainText() {
        return mainText;
    }

    AppCompatEditText mainText;

    public AppCompatEditText getCountryCodeText() {
        return countryCodeText;
    }

    AppCompatEditText countryCodeText;

    AppCompatEditText phoneNumberHint;



    public AppCompatEditText getPhoneNumberHint() {
        return phoneNumberHint;
    }

    public static final int PHONE = 0;
    public static final int MAIL = 1;
    public static final int TEXT = 2;

    public int inputType;
    String mask;

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

    public void init(int inputType) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        this.inputType = inputType;
        mainText = new AppCompatEditText(getContext());
        LayoutParams mainTextLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        mainTextLp.setMargins(0, 0, 0, 0);
        mainText.setBackground(null);
        mainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        switch (inputType) {
            case MAIL:
                mainText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                mainText.setGravity(Gravity.CENTER);
                mainTextLp.setMargins(Sizes.dpToPxExt(4), 0, Sizes.dpToPxExt(4), 0);
                mainText.setLayoutParams(mainTextLp);
                mainText.setSingleLine(true);
                mainText.setMaxLines(1);
                addView(mainText);
                break;
            case TEXT:
                mainText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                mainText.setSingleLine(false);
                mainText.setMaxLines(3);
                mainText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                mainTextLp.setMargins(Sizes.dpToPxExt(4), 0, Sizes.dpToPxExt(4), 0);
                mainText.setLayoutParams(mainTextLp);
                addView(mainText);
                break;
            case PHONE:
                mainTextLp.setMargins(Sizes.dpToPxExt(4), 0, 0, 0);
                countryCodeText = new AppCompatEditText(getContext());
                LayoutParams lp2 = new LayoutParams(Sizes.dpToPxExt(60),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                divider = new View(getContext());
                divider.setLayoutParams(new ViewGroup.LayoutParams(Sizes.dpToPxExt(1),
                        Sizes.dpToPxExt(30)));
                RelativeLayout rl = new RelativeLayout(getContext());
                rl.setLayoutParams(mainTextLp);
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                phoneNumberHint = new AppCompatEditText(getContext());

                lp2.setMargins(0, 0, Sizes.dpToPxExt(4), 0);
                countryCodeText.setLayoutParams(lp2);
                countryCodeText.setBackground(null);
                countryCodeText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
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
                        try {
                            watcher.active = false;
                            mainText.removeTextChangedListener(watcher);
                            watcher = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final CharSequence fs = s;
                        mask = PhoneFormats.getMaskByCode(fs.toString());
                        if (mask != null) {
                            mainText.setFilters(new InputFilter[]{});
                           // mainText.setKeyListener(null);
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
                           // mainText.setKeyListener(new CustomDigitsKeyListener());
                            String text = mainText.getText().toString();
                            text = text.replaceAll(" ", "");
                            mainText.setText(text);
                         /*   if (mainText.getText().toString().isEmpty()) {
                                phoneNumberHint.setHint("");
                            } else {
                                phoneNumberHint.setHint(mainText.getText().toString());
                            }*/
                            if (countryCodeText.getText().length() == 1) {
                                mainText.setHint(baseHint);
                            } else {
                                mainText.setHint("");
                            }

                            mainText.setInputType(InputType.TYPE_CLASS_PHONE);

                            //  ;
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                countryCodeText.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view1, boolean b) {
                        if (!b) return;
                        if (countryCodeText.getText().toString().length() < 1) {
                            countryCodeText.setText("+");
                            countryCodeText.post(new Runnable() {
                                @Override
                                public void run() {
                                    countryCodeText.setSelection(1);
                                }
                            });
                        }
                    }
                });
                mainText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (mask != null) {
                            if (s.length() <= mask.length()) {
                                String s0 = s + mask.substring(s.length());
                                phoneNumberHint.setHint(s0);
                            }
                        } else {
                            if (s.length() < 20) {
                                phoneNumberHint.setHint(s);
                            } else {
                                phoneNumberHint.setHint("");
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                phoneNumberHint.setBackground(null);
                phoneNumberHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // mainText.setElevation(8);
                }
                mainText.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                phoneNumberHint.setClickable(false);
                phoneNumberHint.setFocusable(false);
                phoneNumberHint.setLayoutParams(rlp);
                mainText.setSingleLine(true);
                mainText.setMaxLines(1);
                phoneNumberHint.setSingleLine(true);
                phoneNumberHint.setMaxLines(1);

                rl.addView(phoneNumberHint);
                rl.addView(mainText);
                addView(countryCodeText);
                addView(divider);
                addView(rl);
                break;
        }
    }

    public class CustomDigitsKeyListener extends DigitsKeyListener {
        public CustomDigitsKeyListener() {
            super(false, false);
        }

        public CustomDigitsKeyListener(boolean sign, boolean decimal) {
            super(sign, decimal);
        }

        public int getInputType() {
            return InputType.TYPE_CLASS_PHONE;
        }
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
