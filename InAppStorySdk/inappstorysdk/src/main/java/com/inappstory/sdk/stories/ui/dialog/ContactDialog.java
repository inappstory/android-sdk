package com.inappstory.sdk.stories.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.dialogstructure.DialogStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.SizeStructure;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.ui.widgets.TextMultiInput;
import com.inappstory.sdk.stories.utils.Sizes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.MAIL;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.PHONE;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.TEXT;
import static com.inappstory.sdk.stories.utils.Sizes.isTablet;

public class ContactDialog {


    public DialogStructure dialogStructure;
    String id;
    int storyId;
    SendListener sendListener;
    CancelListener cancelListener;

    public interface CancelListener {
        void onCancel(String id);
    }

    public interface SendListener {
        void onSend(String id, String data);
    }

    public ContactDialog(int storyId, String id, String data,
                         SendListener sendListener, CancelListener cancelListener) {
        this.dialogStructure = JsonParser.fromJson(data, DialogStructure.class);
        this.id = id;
        this.storyId = storyId;
        this.sendListener = sendListener;
        this.cancelListener = cancelListener;
    }

    public static int hex2color(String colorStr) {
        return Color.parseColor(colorStr);
    }

    public double coeff = 1;


    private int flags = 0;


    private void setTypeface(AppCompatTextView textView, boolean bold, boolean italic, boolean secondary) {
        Typeface t = AppearanceManager.getCommonInstance().getFont(secondary, bold, italic);
        int boldV = bold ? 1 : 0;
        int italicV = italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), boldV + italicV);
    }

    private void setTypeface(AppCompatEditText textView, boolean bold, boolean italic, boolean secondary) {
        Typeface t = AppearanceManager.getCommonInstance().getFont(secondary, bold, italic);
        int boldV = bold ? 1 : 0;
        int italicV = italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), boldV + italicV);
    }

    public void showDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.cs_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!isTablet()) {
            dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            dialog.getWindow().setDimAmount(0.05f);
        }
        if (Build.VERSION.SDK_INT < 21) {
            dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            dialog.getWindow().setDimAmount(0.5f);
        }
        final FrameLayout borderContainer = dialog.findViewById(R.id.borderContainer);
        FrameLayout contentContainer = dialog.findViewById(R.id.contentContainer);
        //  contentContainer.setUseCompatPadding(true);
        final FrameLayout editBorderContainer = dialog.findViewById(R.id.editBorderContainer);
        FrameLayout editContainer = dialog.findViewById(R.id.editContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editBorderContainer.setElevation(0f);
            editContainer.setElevation(0f);
            editContainer.setElevation(0f);
        }
        final TextMultiInput editText = dialog.findViewById(R.id.editText);
        String type = dialogStructure.input.type;
        int inttype = TEXT;
        if (type.equals("email")) inttype = MAIL;
        if (type.equals("tel")) inttype = PHONE;
        editText.init(inttype);
        AppCompatTextView text = dialog.findViewById(R.id.text);
        final FrameLayout buttonBackground = dialog.findViewById(R.id.buttonBackground);
        AppCompatTextView buttonText = dialog.findViewById(R.id.buttonText);
        int fullWidth;
        int fullHeight;
        if (isTablet()) {
            fullWidth = activity.getResources().getDimensionPixelSize(R.dimen.cs_tablet_width);
            fullHeight = activity.getResources().getDimensionPixelSize(R.dimen.cs_tablet_height);
        } else {
            fullWidth = Sizes.getScreenSize(activity).x;
            fullHeight = Sizes.getScreenSize(activity).y;
        }
        if (dialogStructure.size == null) {
            dialogStructure.size = new SizeStructure();
            dialogStructure.size.width = 95;
            dialogStructure.size.height = 40;
        }
        final int dialogHeight = (int) ((dialogStructure.size.height / 100) * fullHeight);
        int dialogWidth = (int) ((dialogStructure.size.width / 100) * fullWidth);
        text.setText(dialogStructure.text.value);
        setTypeface(text, dialogStructure.text.isBold(),
                dialogStructure.text.isItalic(),
                dialogStructure.text.isSecondary());
        text.setTextColor(hex2color(dialogStructure.text.color));
        text.setTextSize((int) (coeff * dialogStructure.text.size));
        editText.setHint(dialogStructure.input.text.placeholder);

        editText.setTextColor(hex2color(dialogStructure.input.text.color));
        editText.setHintTextColor(hex2color(dialogStructure.input.text.color));
        editText.setTextSize((int) (coeff * dialogStructure.input.text.size));
        setTypeface(editText.getMainText(), dialogStructure.input.text.isBold(),
                dialogStructure.input.text.isItalic(),
                dialogStructure.input.text.isSecondary());

        if (inttype == PHONE) {

            setTypeface(editText.getCountryCodeText(), dialogStructure.input.text.isBold(),
                    dialogStructure.input.text.isItalic(),
                    dialogStructure.input.text.isSecondary());

            setTypeface(editText.getPhoneNumberHint(), dialogStructure.input.text.isBold(),
                    dialogStructure.input.text.isItalic(),
                    dialogStructure.input.text.isSecondary());
        }

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) editContainer.getLayoutParams();
        int borderWidth = Sizes.dpToPxExt(dialogStructure.input.border.width);
        lp.setMargins(borderWidth,
                borderWidth,
                borderWidth,
                borderWidth);
        editContainer.setLayoutParams(lp);
        buttonText.setText(dialogStructure.button.text.value);
        buttonText.setTextColor(hex2color(dialogStructure.button.text.color));
        buttonText.setTextSize((int) (coeff * dialogStructure.button.text.size));
        setTypeface(buttonText, dialogStructure.button.text.isBold(),
                dialogStructure.button.text.isItalic(),
                dialogStructure.button.text.isSecondary());
        int rad = Sizes.dpToPxExt(dialogStructure.border.radius);

        GradientDrawable buttonBackgroundGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.button.background.color), hex2color(dialogStructure.button.background.color)});
        buttonBackgroundGradient.setCornerRadii(new float[]{0, 0, 0, 0, rad, rad, rad, rad});

        buttonBackground.setBackground(buttonBackgroundGradient);

        final GradientDrawable editBorderContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.input.border.color),
                        hex2color(dialogStructure.input.border.color)});
        editBorderContainerGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.input.border.radius));

        final GradientDrawable editBorderContainerErrorGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{Color.RED,
                        Color.RED});
        editBorderContainerErrorGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.input.border.radius));

        GradientDrawable editContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.input.background.color), hex2color(dialogStructure.input.background.color)});
        editContainerGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.input.border.radius));

        final GradientDrawable borderContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.border.color), hex2color(dialogStructure.border.color)});
        borderContainerGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.border.radius));

        GradientDrawable contentContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.background.color), hex2color(dialogStructure.background.color)});
        contentContainerGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.border.radius));

        editBorderContainer.setBackground(editBorderContainerGradient);
        editContainer.setBackground(editContainerGradient);
        borderContainer.setBackground(borderContainerGradient);
        contentContainer.setBackground(contentContainerGradient);
        if (inttype == PHONE) {
            editText.getDivider().setBackgroundColor(hex2color(dialogStructure.background.color));
        }
        if (inttype == PHONE) {
            editText.getCountryCodeText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    editBorderContainer.setBackground(editBorderContainerGradient);
                    editText.setTextColor(hex2color(dialogStructure.input.text.color));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        editText.getMainText().addTextChangedListener(new TextWatcher() {
            int lastSpecialRequestsCursorPosition;
            String specialRequests;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastSpecialRequestsCursorPosition = editText.getMainText().getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editBorderContainer.setBackground(editBorderContainerGradient);
                editText.setTextColor(hex2color(dialogStructure.input.text.color));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                try {
                    if (str.isEmpty()) {
                        buttonBackground.setVisibility(View.GONE);
                    } else {
                        buttonBackground.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {}

                editText.getMainText().removeTextChangedListener(this);

                if (editText.getMainText().getLineCount() > 3) {
                    editText.getMainText().setText(specialRequests);
                    editText.getMainText().setSelection(lastSpecialRequestsCursorPosition);
                } else
                    specialRequests = editText.getMainText().getText().toString();

                editText.getMainText().addTextChangedListener(this);
            }
        });
        dialog.getWindow().setLayout(dialogWidth, WRAP_CONTENT);
        dialog.show();
        CsEventBus.getDefault().post(new PauseStoryReaderEvent(false));
        final AppCompatEditText et;
        if (inttype == PHONE) {
            et = editText.getCountryCodeText();
        } else {
            et = editText.getMainText();
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                View view = activity.getCurrentFocus();
                editText.clearFocus();
                if (view != null) {

                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    CsEventBus.getDefault().post(new ResumeStoryReaderEvent(true));
                }


            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }, 100);
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                cancelListener.onCancel(id);
            }
        });
        final int finalInttype = inttype;
        buttonBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate(finalInttype, editText.getMainText().getText().toString(),
                        editText.getMaskLength())) {
                    dialog.dismiss();
                    String val = editText.getText().replaceAll("\"", "\\\\\"");
                    //URLEncoder.encode(val, StandardCharsets.UTF_8.toString());
                    sendListener.onSend(id, val);
                } else {
                    editBorderContainer.setBackground(editBorderContainerErrorGradient);
                    editText.setTextColor(Color.RED);
                }
            }
        });

        if (!isTablet()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    et.requestFocus();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 200);
        }
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

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
