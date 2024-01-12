package com.inappstory.sdk.inputdialog.ui;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.models.DialogData;
import com.inappstory.sdk.core.models.DialogType;
import com.inappstory.sdk.core.models.js.dialogstructure.ButtonStructure;
import com.inappstory.sdk.core.models.js.dialogstructure.CenterStructure;
import com.inappstory.sdk.core.models.js.dialogstructure.DialogStructure;
import com.inappstory.sdk.core.models.js.dialogstructure.InputStructure;
import com.inappstory.sdk.core.models.js.dialogstructure.QuestionStructure;
import com.inappstory.sdk.core.models.js.dialogstructure.SizeStructure;
import com.inappstory.sdk.databinding.CsDialogLayoutBinding;
import com.inappstory.sdk.inputdialog.uidomain.IInputDialogViewModel;
import com.inappstory.sdk.inputdialog.uidomain.KeyboardState;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.utils.Sizes;


public class InputDialogFragment extends Fragment {

    IInputDialogViewModel dialogViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogViewModel = IASUICore.getInstance().getStoriesReaderVM().getDialogViewModel();
    }

    CsDialogLayoutBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogViewModel.keyboardState().observe(getViewLifecycleOwner(), keyboardStateObserver);
        dialogViewModel.currentDialogData().observe(getViewLifecycleOwner(), dialogDataObserver);
    }

    @Override
    public void onDestroyView() {
        dialogViewModel.currentDialogData().removeObserver(dialogDataObserver);
        dialogViewModel.keyboardState().removeObserver(keyboardStateObserver);
        super.onDestroyView();
    }

    private final Observer<KeyboardState> keyboardStateObserver = new Observer<KeyboardState>() {
        @Override
        public void onChanged(KeyboardState keyboardState) {

        }
    };

    private final Observer<DialogData> dialogDataObserver = new Observer<DialogData>() {
        @Override
        public void onChanged(DialogData data) {
            if (data == null) {
                binding.getRoot().setVisibility(View.GONE);
            } else {
                bindViews(data);
                binding.getRoot().setVisibility(View.VISIBLE);
            }
        }
    };

    private void setTypeface(AppCompatTextView textView, boolean bold, boolean italic, boolean secondary) {
        Typeface t = AppearanceManager.getCommonInstance().getFont(secondary, bold, italic);
        int boldV = bold ? 1 : 0;
        int italicV = italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), boldV + italicV);
    }

    private void setTypeface(IInputDialogTextField textView, boolean bold, boolean italic, boolean secondary) {
        Typeface t = AppearanceManager.getCommonInstance().getFont(secondary, bold, italic);
        int boldV = bold ? 1 : 0;
        int italicV = italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), boldV + italicV);
    }

    int fullWidth;
    int fullHeight;
    int dialogHeight;
    int dialogWidth;
    CenterStructure startedCenterStructure;
    CenterStructure newCenterStructure;
    CenterStructure currentCenterStructure;
    FrameLayout dialogArea;

    private void bindViews(DialogData data) {
        DialogStructure dialogStructure = data.dialogStructure();


        if (Sizes.isTablet(getContext())) {
            fullWidth = getContext().getResources().getDimensionPixelSize(R.dimen.cs_tablet_width);
            fullHeight = getContext().getResources().getDimensionPixelSize(R.dimen.cs_tablet_height);
        } else {
            fullWidth = Sizes.getScreenSize(getContext()).x;
            fullHeight = Sizes.getScreenSize(getContext()).y;
        }
        if (dialogStructure.size == null) {
            dialogStructure.size = new SizeStructure();
            dialogStructure.size.width = 95;
            dialogStructure.size.height = 40;
        }
        dialogHeight = (int) ((dialogStructure.size.height / 100) * fullHeight);
        dialogWidth = (int) ((dialogStructure.size.width / 100) * fullWidth);
        factor = (1f * fullWidth) / dialogStructure.configV2.factor;
        contentContainer.setPaddingRelative(
                getSize(dialogStructure.configV2.main.padding.left),
                getSize(dialogStructure.configV2.main.padding.top),
                getSize(dialogStructure.configV2.main.padding.right),
                getSize(dialogStructure.configV2.main.padding.bottom));


        startedCenterStructure = dialogStructure.size.center;
        currentCenterStructure = dialogStructure.size.center;
        if (startedCenterStructure == null) startedCenterStructure = new CenterStructure(50, 50);
        newCenterStructure = new CenterStructure(50, 50);
        FrameLayout.LayoutParams dialogAreaParams = new FrameLayout.LayoutParams(dialogWidth, WRAP_CONTENT);
        int topMargin = (int) (fullHeight * startedCenterStructure.y / 100 - dialogHeight / 2);
        int leftMargin = (int) (fullWidth * startedCenterStructure.x / 100 - dialogWidth / 2);
        dialogAreaParams.setMargins(leftMargin, topMargin, 0, 0);
        int radius = getSize(dialogStructure.configV2.main.border.radius);
        binding.dialogArea.setLayoutParams(dialogAreaParams);


        final GradientDrawable borderContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.configV2.main.border.color),
                        hex2color(dialogStructure.configV2.main.border.color)});
        borderContainerGradient.setCornerRadius(radius);

        GradientDrawable parentContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(dialogStructure.configV2.main.background.color),
                        hex2color(dialogStructure.configV2.main.background.color)});
        parentContainerGradient.setCornerRadius(radius);

        borderContainer.setBackground(borderContainerGradient);
        parentContainer.setBackground(parentContainerGradient);
        if (inttype == PHONE) {
            editText.getDivider().setBackgroundColor(
                    hex2color(dialogStructure.configV2.main.background.color));
        }
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = CsDialogLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void createQuestion(DialogData data) {
        QuestionStructure questionStructure = data.dialogStructure().configV2.main.question;
        if (questionStructure == null) return;
        if (questionStructure.text.value.isEmpty()) {
            binding.text.setVisibility(View.GONE);
        } else {
            binding.text.setText(questionStructure.text.value);
            binding.text.setTextColor(hex2color(questionStructure.text.color));
            binding.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSize(questionStructure.text.size));
            binding.text.setPaddingRelative(
                    getSize(questionStructure.padding.left),
                    getSize(questionStructure.padding.top),
                    getSize(questionStructure.padding.right),
                    getSize(questionStructure.padding.bottom)
            );
            binding.text.setLineSpacing(0, questionStructure.text.lineHeight / questionStructure.text.size);
        }
        setTypeface(binding.text, questionStructure.text.isBold(),
                questionStructure.text.isItalic(),
                questionStructure.text.isSecondary());

        switch (questionStructure.text.align) {
            case "right":
                ((LinearLayout.LayoutParams) binding.text.getLayoutParams()).gravity =
                        Gravity.RIGHT;
                binding.text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            case "center":
                ((LinearLayout.LayoutParams) binding.text.getLayoutParams()).gravity =
                        Gravity.CENTER_HORIZONTAL;
                binding.text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                break;
            default:
                binding.text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                break;
        }

    }

    private int hex2color(String colorStr) {
        return Color.parseColor(colorStr);
    }

    private float factor = 1f;

    private int getSize(float size) {
        return (int) (factor * size);
    }

    private void createInput(DialogData data) {
        InputStructure inputStructure = data.dialogStructure().configV2.main.input;
        if (inputStructure == null) return;
        binding.editContainer.removeAllViewsInLayout();

        binding.editBorderContainer.setElevation(0f);
        binding.editContainer.setElevation(0f);


        final GradientDrawable editBorderContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(inputStructure.border.color),
                        hex2color(inputStructure.border.color)});
        editBorderContainerGradient.setCornerRadius(getSize(inputStructure.border.radius));

        final GradientDrawable editBorderContainerErrorGradient =
                new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                        new int[]{
                                Color.RED,
                                Color.RED
                        }
                );
        editBorderContainerErrorGradient.setCornerRadius(
                getSize(inputStructure.border.radius));

        GradientDrawable editContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{hex2color(inputStructure.background.color),
                        hex2color(inputStructure.background.color)});
        editContainerGradient.setCornerRadius(getSize(inputStructure.border.radius));

        binding.editBorderContainer.setBackground(editBorderContainerGradient);
        binding.editContainer.setBackground(editContainerGradient);

        int borderWidth = getSize(inputStructure.border.width);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) binding.editContainer.getLayoutParams();
        lp.setMargins(
                borderWidth,
                borderWidth,
                borderWidth,
                borderWidth
        );
        binding.editContainer.setLayoutParams(lp);
        binding.editBorderContainer.setPaddingRelative(
                borderWidth,
                borderWidth,
                borderWidth,
                borderWidth
        );
        binding.editContainer.setPaddingRelative(
                getSize(inputStructure.padding.left),
                getSize(inputStructure.padding.top),
                getSize(inputStructure.padding.right),
                getSize(inputStructure.padding.bottom)
        );

        IInputDialogTextField textField;
        switch (data.dialogType()) {
            case PHONE:
                textField = new InputDialogPhoneField(getContext());
                break;
            case MAIL:
                textField = new InputDialogMailField(getContext());
                break;
            default:
                textField = new InputDialogPlainTextField(getContext());
                break;
        }

        if (data.dialogType() != DialogType.PHONE) {
            AppCompatEditText editText = (AppCompatEditText) textField;
            editText.setLineSpacing(
                    0,
                    inputStructure.text.lineHeight / inputStructure.text.size
            );
            switch (inputStructure.text.align) {
                case "right":
                    editText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    break;
                case "center":
                    editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    break;
                default:
                    editText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    break;
            }
        }

        textField.setHint(inputStructure.text.placeholder);
        textField.setTextColor(hex2color(inputStructure.text.color));
        textField.setHintTextColor(hex2color(inputStructure.text.color));
        textField.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getSize(inputStructure.text.size));
        binding.editContainer.addView((View) textField);
    }

    private void createButton(DialogData data) {
        ButtonStructure buttonStructure = data.dialogStructure().configV2.main.button;
        if (buttonStructure == null) return;
        int radius = getSize(data.dialogStructure().configV2.main.border.radius);
        binding.buttonText.setPaddingRelative(
                getSize(buttonStructure.padding.left),
                getSize(buttonStructure.padding.top),
                getSize(buttonStructure.padding.right),
                getSize(buttonStructure.padding.bottom)
        );
        binding.buttonText.setText(buttonStructure.text.value);
        binding.buttonText.setTextColor(hex2color(buttonStructure.text.color));
        binding.buttonText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getSize(buttonStructure.text.size));
        binding.buttonText.setLineSpacing(0,
                buttonStructure.text.lineHeight /
                        buttonStructure.text.size);
        setTypeface(binding.buttonText, buttonStructure.text.isBold(),
                buttonStructure.text.isItalic(),
                buttonStructure.text.isSecondary());
        switch (buttonStructure.text.align) {
            case "right":
                binding.buttonText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            case "center":
                binding.buttonText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                break;
            default:
                binding.buttonText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                break;
        }

        GradientDrawable buttonBackgroundGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[]{
                        hex2color(buttonStructure.background.color),
                        hex2color(buttonStructure.background.color)
                });
        buttonBackgroundGradient.setCornerRadii(new float[]{0, 0, 0, 0, radius, radius, radius, radius});

        binding.buttonBackground.setBackground(buttonBackgroundGradient);
    }
}
