package com.inappstory.sdk.stories.ui.dialog;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.MAIL;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.PHONE;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.TEXT;
import static com.inappstory.sdk.stories.utils.Sizes.isTablet;

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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.dialogstructure.CenterStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.DialogStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.SizeStructure;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.widgets.TextMultiInput;
import com.inappstory.sdk.stories.utils.Sizes;

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

    private int getSize(float size) {
        return (int) (factor * size);
    }

    float factor = 1;

    public void showDialog(final Activity activity) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showDialogInner(activity);
            }
        });
    }

    private void showDialogInner(final Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.StoriesSDKAppTheme_DialogTheme);
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

        //  contentContainer.setUseCompatPadding(true);
        final FrameLayout editBorderContainer = dialog.findViewById(R.id.editBorderContainer);
        FrameLayout editContainer = dialog.findViewById(R.id.editContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editBorderContainer.setElevation(0f);
            editContainer.setElevation(0f);
            editContainer.setElevation(0f);
        }
        final TextMultiInput editText = dialog.findViewById(R.id.editText);
        String type = dialogStructure.configV2.main.input.type;
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


        factor = (1f * fullWidth) / dialogStructure.configV2.factor;
        LinearLayout parentContainer = dialog.findViewById(R.id.parentContainer);
        LinearLayout contentContainer = dialog.findViewById(R.id.contentContainer);
        contentContainer.setPaddingRelative(
                getSize(dialogStructure.configV2.main.padding.left),
                getSize(dialogStructure.configV2.main.padding.top),
                getSize(dialogStructure.configV2.main.padding.right),
                getSize(dialogStructure.configV2.main.padding.bottom));
        if (dialogStructure.configV2.main.question.text.value.isEmpty()) {
            text.setVisibility(View.GONE);
        } else {
            text.setText(dialogStructure.configV2.main.question.text.value);
            text.setTextColor(hex2color(dialogStructure.configV2.main.question.text.color));
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSize(dialogStructure.configV2.main.question.text.size));
            text.setPaddingRelative(
                    getSize(dialogStructure.configV2.main.question.padding.left),
                    getSize(dialogStructure.configV2.main.question.padding.top),
                    getSize(dialogStructure.configV2.main.question.padding.right),
                    getSize(dialogStructure.configV2.main.question.padding.bottom)
            );
            text.setLineSpacing(0,
                    dialogStructure.configV2.main.question.text.lineHeight /
                            dialogStructure.configV2.main.question.text.size);
        }
        setTypeface(text, dialogStructure.configV2.main.question.text.isBold(),
                dialogStructure.configV2.main.question.text.isItalic(),
                dialogStructure.configV2.main.question.text.isSecondary());

        switch (dialogStructure.configV2.main.question.text.align) {
            case "right":
                ((LinearLayout.LayoutParams) text.getLayoutParams()).gravity = Gravity.RIGHT;
                text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            case "center":
                ((LinearLayout.LayoutParams) text.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
                text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                break;
            default:
                text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                break;
        }

        editText.setHint(dialogStructure.configV2.main.input.text.placeholder);
        editText.setTextColor(hex2color(dialogStructure.configV2.main.input.text.color));
        editText.setHintTextColor(hex2color(dialogStructure.configV2.main.input.text.color));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getSize(dialogStructure.configV2.main.input.text.size));

        editBorderContainer.setPaddingRelative(
                getSize(dialogStructure.configV2.main.input.padding.left),
                getSize(dialogStructure.configV2.main.input.padding.top),
                getSize(dialogStructure.configV2.main.input.padding.right),
                getSize(dialogStructure.configV2.main.input.padding.bottom)
        );
        if (inttype != PHONE) {
            editText.getMainText().setLineSpacing(0,
                    dialogStructure.configV2.main.input.text.lineHeight /
                            dialogStructure.configV2.main.input.text.size);
            switch (dialogStructure.configV2.main.input.text.align) {
                case "right":
                    editText.getMainText().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    break;
                case "center":
                    editText.getMainText().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    break;
                default:
                    editText.getMainText().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    break;
            }
        } else  {
            editText.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setTypeface(editText.getMainText(), dialogStructure.configV2.main.input.text.isBold(),
                dialogStructure.configV2.main.input.text.isItalic(),
                dialogStructure.configV2.main.input.text.isSecondary());

        if (inttype == PHONE) {

            setTypeface(editText.getCountryCodeText(), dialogStructure.configV2.main.input.text.isBold(),
                    dialogStructure.configV2.main.input.text.isItalic(),
                    dialogStructure.configV2.main.input.text.isSecondary());

            setTypeface(editText.getPhoneNumberHint(), dialogStructure.configV2.main.input.text.isBold(),
                    dialogStructure.configV2.main.input.text.isItalic(),
                    dialogStructure.configV2.main.input.text.isSecondary());
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) editContainer.getLayoutParams();
        int borderWidth = getSize(dialogStructure.configV2.main.input.border.width);
        lp.setMargins(borderWidth,
                borderWidth,
                borderWidth,
                borderWidth);
        editContainer.setLayoutParams(lp);
        CenterStructure centerStructure = dialogStructure.size.center;
        if (centerStructure == null) centerStructure = new CenterStructure(50, 50);
        RelativeLayout.LayoutParams parentParams = new RelativeLayout.LayoutParams(dialogWidth, WRAP_CONTENT);
        int topMargin = (int) (fullHeight * centerStructure.y / 100 - dialogHeight / 2);
        int bottomMargin = (int) (fullHeight * (100 - centerStructure.y) / 100 - dialogHeight / 2);
        int leftMargin = (int) (fullWidth * centerStructure.x / 100 - dialogWidth / 2);
        int rightMargin = (int) (fullWidth * (100 - centerStructure.x) / 100 - dialogWidth / 2);
        parentParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        //parentContainer.setLayoutParams(parentParams);
        buttonText.setPaddingRelative(
                getSize(dialogStructure.configV2.main.button.padding.left),
                getSize(dialogStructure.configV2.main.button.padding.top),
                getSize(dialogStructure.configV2.main.button.padding.right),
                getSize(dialogStructure.configV2.main.button.padding.bottom)
        );
        buttonText.setText(dialogStructure.configV2.main.button.text.value);
        buttonText.setTextColor(hex2color(dialogStructure.configV2.main.button.text.color));
        buttonText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getSize(dialogStructure.configV2.main.button.text.size));
        buttonText.setLineSpacing(0,
                dialogStructure.configV2.main.button.text.lineHeight /
                        dialogStructure.configV2.main.button.text.size);
        setTypeface(buttonText, dialogStructure.configV2.main.button.text.isBold(),
                dialogStructure.configV2.main.button.text.isItalic(),
                dialogStructure.configV2.main.button.text.isSecondary());
        switch (dialogStructure.configV2.main.button.text.align) {
            case "right":
                buttonText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            case "center":
                buttonText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                break;
            default:
                buttonText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                break;
        }
        int rad = getSize(dialogStructure.configV2.main.border.radius);

        GradientDrawable buttonBackgroundGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.configV2.main.button.background.color),
                        hex2color(dialogStructure.configV2.main.button.background.color)});
        buttonBackgroundGradient.setCornerRadii(new float[]{0, 0, 0, 0, rad, rad, rad, rad});

        buttonBackground.setBackground(buttonBackgroundGradient);

        final GradientDrawable editBorderContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.configV2.main.input.border.color),
                        hex2color(dialogStructure.configV2.main.input.border.color)});
        editBorderContainerGradient.setCornerRadius(getSize(dialogStructure.configV2.main.input.border.radius));

        final GradientDrawable editBorderContainerErrorGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{Color.RED,
                        Color.RED});
        editBorderContainerErrorGradient.setCornerRadius(
                getSize(dialogStructure.configV2.main.input.border.radius));

        GradientDrawable editContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.configV2.main.input.background.color),
                        hex2color(dialogStructure.configV2.main.input.background.color)});
        editContainerGradient.setCornerRadius(getSize(dialogStructure.configV2.main.input.border.radius));

        final GradientDrawable borderContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.configV2.main.border.color),
                        hex2color(dialogStructure.configV2.main.border.color)});
        borderContainerGradient.setCornerRadius(
                getSize(dialogStructure.configV2.main.border.radius));

        GradientDrawable parentContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.configV2.main.background.color),
                        hex2color(dialogStructure.configV2.main.background.color)});
        parentContainerGradient.setCornerRadius(
                getSize(dialogStructure.configV2.main.border.radius));

        editBorderContainer.setBackground(editBorderContainerGradient);
        editContainer.setBackground(editContainerGradient);
        borderContainer.setBackground(borderContainerGradient);
        parentContainer.setBackground(parentContainerGradient);
        if (inttype == PHONE) {
               editText.getDivider().setBackgroundColor(
                    hex2color(dialogStructure.configV2.main.background.color));
        }
        if (inttype == PHONE) {
            editText.getCountryCodeText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    editBorderContainer.setBackground(editBorderContainerGradient);
                    editText.setTextColor(hex2color(dialogStructure.configV2.main.input.text.color));
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
                editText.setTextColor(hex2color(dialogStructure.configV2.main.input.text.color));
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
                } catch (Exception e) {
                }

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
        StatisticManager.getInstance().pauseStoryEvent(false);
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
                if (view != null) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            editText.clearFocus();
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            StatisticManager.getInstance().resumeStoryEvent(true);
                        }
                    });
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
            View view = activity.getCurrentFocus();
            if (view != null) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        et.requestFocus();
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 200);
            }
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
