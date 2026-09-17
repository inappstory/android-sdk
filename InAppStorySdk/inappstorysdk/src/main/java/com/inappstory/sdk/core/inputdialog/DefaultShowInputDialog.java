package com.inappstory.sdk.core.inputdialog;

import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.MAIL;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.PHONE;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.TEXT;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.inappstory.sdk.stories.ui.widgets.TextMultiInput;
import com.inappstory.sdk.stories.utils.Sizes;

public class DefaultShowInputDialog implements IShowInputDialog {
    @Override
    public void onShow(
            Context context,
            InputDialogData dialogData,
            InputDialogSource source,
            IInputDialogSubmit submitCallback
    ) {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
        alertdialog.setTitle(dialogData.title);
        final TextMultiInput textField = new TextMultiInput(context);
        final int inttype;
        if (dialogData.type.equals("email")) inttype = MAIL;
        else if (dialogData.type.equals("tel")) inttype = PHONE;
        else inttype = TEXT;
        textField.init(inttype, 3);
        int textColor = textField.getMainText().getCurrentTextColor();
        if (inttype == PHONE) {
            textField.getMainText().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            textField.getDivider().setBackgroundColor(Color.TRANSPARENT);
        } else {
            textField.getMainText().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int verticalPadding = Sizes.dpToPxExt(8, context);
        int horizontalPadding = Sizes.dpToPxExt(24, context);
        layoutParams.setMargins(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding); //set margin

        LinearLayout lp = new LinearLayout(context);
        lp.setOrientation(LinearLayout.VERTICAL);
        //textField.setPadding(Sizes.dpToPxExt(16, context));
        lp.addView(textField, layoutParams);
        View underline = new View(context);
        underline.setBackgroundColor(textColor);
        LinearLayout.LayoutParams underlineUnfocusedLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, Sizes.dpToPxExt(1, context));
        LinearLayout.LayoutParams underlineFocusedLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, Sizes.dpToPxExt(2, context));
        underlineUnfocusedLayoutParams.setMargins(horizontalPadding, 0, horizontalPadding, verticalPadding);
        underlineFocusedLayoutParams.setMargins(horizontalPadding, 0, horizontalPadding, verticalPadding);
        underline.setLayoutParams(underlineUnfocusedLayoutParams);
        lp.addView(underline);
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textField.setTextColor(textColor);
                underline.setBackgroundColor(textColor);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    underline.setLayoutParams(underlineFocusedLayoutParams);
                    showKeyboard(v);
                } else {
                    underline.setLayoutParams(underlineUnfocusedLayoutParams);
                    hideKeyboard(v);
                }
            }
        });
        alertdialog.setView(lp);
        if (dialogData.submitButton != null && !dialogData.submitButton.isEmpty()) {
            alertdialog.setPositiveButton(dialogData.submitButton, (dialogInterface, i) -> {

            });
        }
        if (dialogData.negativeButton != null && !dialogData.negativeButton.isEmpty()) {
            alertdialog.setNegativeButton(dialogData.negativeButton, (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
        }
        if (dialogData.neutralButton != null && !dialogData.neutralButton.isEmpty()) {
            alertdialog.setNeutralButton(dialogData.neutralButton, (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
        }
        AlertDialog alert = alertdialog.create();
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                submitCallback.onDismiss();
            }
        });
        // alert.setCanceledOnTouchOutside(true);
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textField.requestFocusField();
                    }
                }, 300);
            }
        });
        alert.show();
        Button positiveAlertButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);

        positiveAlertButton.setOnClickListener(v -> {
            if (validate(inttype, textField.getText(), textField.getMaskLength())) {
                alert.dismiss();
                submitCallback.onSubmit(textField.getText());
            } else {
                textField.setTextColor(Color.RED);
                underline.setBackgroundColor(Color.RED);
            }
        });




    }

    boolean validate(int type, String value, int length) {
        if (type == PHONE) {
            if (length > 0)
                return value.length() == length;
            else {
                if (value != null && value.length() >= 5 && value.length() <= 30)
                    return true;
                return false;
            }
        } else if (type == MAIL) {
            return isValidEmail(value);
        } else {
            return true;
        }
    }



    private void showKeyboard(View view) {
        // view.requestFocus();
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public final boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
