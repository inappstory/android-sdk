package com.inappstory.sdk.stories.ui.dialog;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.MAIL;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.PHONE;
import static com.inappstory.sdk.stories.ui.widgets.TextMultiInput.TEXT;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.stories.api.models.dialogstructure.CenterStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.DialogStructure;
import com.inappstory.sdk.stories.api.models.dialogstructure.SizeStructure;
import com.inappstory.sdk.stories.ui.widgets.TextMultiInput;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.Sizes;

public class ContactDialogFragment extends Fragment implements IASBackPressHandler {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        Bundle arguments = requireArguments();
        dialogStructure = (DialogStructure) arguments.getSerializable(DialogStructure.SERIALIZABLE_KEY);
        dialogId = arguments.getString("dialogId");
        storyId = arguments.getInt("storyId");
        dialogContainerSize = new Point(
                arguments.getInt("dialogContainerWidth"),
                arguments.getInt("dialogContainerHeight")
        );
        storyId = arguments.getInt("storyId");
        return inflater.inflate(R.layout.cs_dialog_layout, container, false);
    }

    private DialogStructure dialogStructure;
    String dialogId;
    int storyId;
    Point dialogContainerSize;

    public static final String TAG = "IASDialogFragment";

    public SendListener sendListener;
    public CancelListener cancelListener;

    public ContactDialogFragment() {
    }

    public static int hex2color(String colorStr) {
        return ColorUtils.parseColorRGBA(colorStr);
    }

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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        if (getActivity() != null)
            getActivity().getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendListener = null;
        cancelListener = null;
    }

    @Override
    public void onDestroyView() {
        //   if (getView() != null && globalLayoutListener != null)
        if (getActivity() != null)
            getActivity().getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);

        super.onDestroyView();
    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;


    private final int defaultKeyboardHeightDP = 100;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dialogContainerSize == null) {
            if (Sizes.isTablet(getContext())) {
                dialogContainerSize = new Point(
                        Sizes.dpToPxExt(340, getContext()),
                        Sizes.dpToPxExt(640, getContext())
                );
            } else {
                dialogContainerSize = Sizes.getScreenSize(getContext());
            }
        }
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean alreadyOpen;
            private final Rect rect = new Rect();
            private Runnable checkRunnable = new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                        int size = dialogContainerSize.y - rect.height();

                        boolean isShown = size >= Sizes.dpToPxExt(defaultKeyboardHeightDP, getActivity());
                        if (isShown == alreadyOpen) {
                            return;
                        }
                        alreadyOpen = isShown;
                        if (isShown) {
                            newCenterStructure = new CenterStructure(
                                    50f,
                                    50f * (dialogContainerSize.y - size) /
                                            dialogContainerSize.y);

                        } else {
                            if (cancelListener != null) {
                                cancelListener.onCancel(dialogId);
                                hideDialog();
                            }
                        }
                        keyboardIsShown = isShown;
                        if (newCenterStructure.y != currentCenterStructure.y) {
                            animateDialogArea();
                        }
                    }
                }
            };
            private Handler keyboardHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onGlobalLayout() {
                keyboardHandler.removeCallbacks(checkRunnable);
                keyboardHandler.post(checkRunnable);
            }
        };
    }

    boolean keyboardIsShown = false;

    ValueAnimator animator;

    private void animateDialogArea() {
        animator = ValueAnimator.ofFloat(currentCenterStructure.y, newCenterStructure.y);
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) dialogArea.getLayoutParams();
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                layoutParams.topMargin =
                        (int) (fullHeight * value / 100 - dialogHeight / 2);
                dialogArea.requestLayout();
                currentCenterStructure.y = value;
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimated = true;
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimated = false;
                animator = null;
                currentCenterStructure = newCenterStructure;
                super.onAnimationEnd(animation);
            }
        });
        animator.start();
    }

    boolean isAnimated = true;

    int fullWidth;
    int fullHeight;
    int dialogHeight;
    int dialogWidth;

    CenterStructure startedCenterStructure;
    CenterStructure newCenterStructure;
    CenterStructure currentCenterStructure;
    FrameLayout dialogArea;

    private void showKeyboard(View view) {
        // view.requestFocus();
        if (getContext() == null) return;
        InputMethodManager imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    TextMultiInput editText = null;

    private void bindViews(@NonNull View dialog) {
        FrameLayout borderContainer = dialog.findViewById(R.id.borderContainer);
        final FrameLayout editBorderContainer = dialog.findViewById(R.id.editBorderContainer);

        FrameLayout editContainer = dialog.findViewById(R.id.editContainer);
        editBorderContainer.setElevation(0f);
        editContainer.setElevation(0f);
        editText = dialog.findViewById(R.id.editText);
        String type = dialogStructure.configV2.main.input.type;
        final int limit = dialogStructure.configV2.main.input.limit();
        int maxLines = dialogStructure.configV2.main.input.maxLines();
        int inttype = TEXT;
        if (type.equals("email")) inttype = MAIL;
        if (type.equals("tel")) inttype = PHONE;
        editText.init(inttype, maxLines);
        AppCompatTextView text = dialog.findViewById(R.id.text);
        final FrameLayout buttonBackground = dialog.findViewById(R.id.buttonBackground);
        AppCompatTextView buttonText = dialog.findViewById(R.id.buttonText);

        fullWidth = dialogContainerSize.x;
        fullHeight = dialogContainerSize.y;
        if (dialogStructure.size == null) {
            dialogStructure.size = new SizeStructure();
            dialogStructure.size.width = 95;
            dialogStructure.size.height = 40;
        }
        dialogHeight = (int) ((dialogStructure.size.height / 100) * fullHeight);
        dialogWidth = (int) ((dialogStructure.size.width / 100) * fullWidth);
        factor = (1f * fullWidth) / dialogStructure.configV2.factor;
        LinearLayout parentContainer = dialog.findViewById(R.id.parentContainer);
        dialogArea = dialog.findViewById(R.id.dialogArea);
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
                getSize(dialogStructure.configV2.main.input.border.width),
                getSize(dialogStructure.configV2.main.input.border.width),
                getSize(dialogStructure.configV2.main.input.border.width),
                getSize(dialogStructure.configV2.main.input.border.width)
        );
        editContainer.setPaddingRelative(
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
        } else {
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
        lp.setMargins(
                borderWidth,
                borderWidth,
                borderWidth,
                borderWidth
        );
        editContainer.setLayoutParams(lp);
        startedCenterStructure = dialogStructure.size.center;
        currentCenterStructure = dialogStructure.size.center;
        if (startedCenterStructure == null) startedCenterStructure = new CenterStructure(50, 50);
        newCenterStructure = new CenterStructure(startedCenterStructure.x, startedCenterStructure.y);
        FrameLayout.LayoutParams dialogAreaParams = new FrameLayout.LayoutParams(dialogWidth, WRAP_CONTENT);
        int topMargin = (int) (fullHeight * startedCenterStructure.y / 100 - dialogHeight / 2);
        int leftMargin = (int) (fullWidth * startedCenterStructure.x / 100 - dialogWidth / 2);
        dialogAreaParams.setMargins(leftMargin, topMargin, 0, 0);
        dialogArea.setLayoutParams(dialogAreaParams);
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
                Editable editableText = editText.getMainText().getText();
                if (editableText != null) {
                    if (editableText.length() > limit) {
                        editText.getMainText().setText(specialRequests);
                        editText.getMainText().setSelection(lastSpecialRequestsCursorPosition);
                    } else {
                        specialRequests = editableText.toString();
                    }
                }
                editText.getMainText().addTextChangedListener(this);
            }
        });
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.statistic().storiesV2().pauseStoryEvent(false);
            }
        });
        final int finalInttype = inttype;
        buttonBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate(finalInttype, editText.getMainText().getText().toString(),
                        editText.getMaskLength())) {
                    if (sendListener != null) {
                        String val = editText.getText().replaceAll("\"", "\\\\\"");
                        sendListener.onSend(dialogId, val);
                        hideKeyboard();
                        hideDialog();
                    }
                } else {
                    editBorderContainer.setBackground(editBorderContainerErrorGradient);
                    editText.setTextColor(Color.RED);
                }
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(v);
                } else {
                    hideKeyboard(v);
                }
            }
        });
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocusField();
            }
        });
        animateDialogArea();
        dialog.findViewById(R.id.emptyArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hideKeyboard() && cancelListener != null) {
                    cancelListener.onCancel(dialogId);
                    hideDialog();
                }
            }
        });
    }

    private boolean hideKeyboard() {
        if (keyboardIsShown) {
            if (editText != null) editText.clearFocus();
        }
        return true;
    }

    private void hideDialog() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.statistic().storiesV2().resumeStoryEvent(true);
            }
        });
        getParentFragmentManager().popBackStack();
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


    @Override
    public boolean onBackPressed() {
        if (hideKeyboard() && cancelListener != null) {
            cancelListener.onCancel(dialogId);
            hideDialog();
        }
        return true;
    }
}
