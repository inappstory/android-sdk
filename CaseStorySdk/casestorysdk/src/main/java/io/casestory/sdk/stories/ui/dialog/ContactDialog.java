package io.casestory.sdk.stories.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.network.JsonParser;
import io.casestory.sdk.stories.utils.Sizes;

public class ContactDialog {

    class DialogStructure {

        public BackgroundStructure background;
        public BorderStructure border;
        public TextStructure text;
        public InputStructure input;
        public ButtonStructure button;


        class BackgroundStructure {
            public String color;
        }

        class BorderStructure {
            public int radius;
            public int width;
            public String color;
        }

        class TextStructure {
            public int size;
            public String color;
            public String value;
            public String placeholder;
        }

        class InputStructure {
            public BackgroundStructure background;
            public BorderStructure border;
            public TextStructure text;
        }

        class ButtonStructure {
            public BackgroundStructure background;
            public TextStructure text;
        }
    }

    DialogStructure dialogStructure;
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
    public void showDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.cs_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!Sizes.isTablet()) {
            dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            dialog.getWindow().setDimAmount(0.0f);
        }
        if (Build.VERSION.SDK_INT < 21) {
            dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            dialog.getWindow().setDimAmount(0.5f);
        }
        FrameLayout borderContainer = dialog.findViewById(R.id.borderContainer);
        FrameLayout contentContainer = dialog.findViewById(R.id.contentContainer);
      //  contentContainer.setUseCompatPadding(true);
        FrameLayout editBorderContainer = dialog.findViewById(R.id.editBorderContainer);
        FrameLayout editContainer = dialog.findViewById(R.id.editContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editBorderContainer.setElevation(0f);
            editContainer.setElevation(0f);
            editContainer.setElevation(0f);
        }
        final AppCompatEditText editText = dialog.findViewById(R.id.editText);
        AppCompatTextView text = dialog.findViewById(R.id.text);
        final FrameLayout buttonBackground = dialog.findViewById(R.id.buttonBackground);
        AppCompatTextView buttonText = dialog.findViewById(R.id.buttonText);


        text.setText(dialogStructure.text.value);
        text.setTextColor(hex2color(dialogStructure.text.color));
        text.setTextSize((int) (coeff * dialogStructure.text.size));
        editText.setHint(dialogStructure.input.text.placeholder);
        editText.setTextColor(hex2color(dialogStructure.input.text.color));
        editText.setHintTextColor(hex2color(dialogStructure.input.text.color));
        editText.setTextSize((int) (coeff * dialogStructure.input.text.size));
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) editContainer.getLayoutParams();
        lp.setMargins(dialogStructure.input.border.width,
                dialogStructure.input.border.width,
                dialogStructure.input.border.width,
                dialogStructure.input.border.width);
        editContainer.setLayoutParams(lp);
        buttonText.setText(dialogStructure.button.text.value);
        buttonText.setTextColor(hex2color(dialogStructure.button.text.color));
        buttonText.setTextSize((int) (coeff * dialogStructure.button.text.size));

        int rad = Sizes.dpToPxExt(dialogStructure.border.radius);

        GradientDrawable buttonBackgroundGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[] {hex2color(dialogStructure.button.background.color),hex2color(dialogStructure.button.background.color)});
        buttonBackgroundGradient.setCornerRadii(new float[]{0, 0, 0, 0, rad, rad, rad, rad});

        buttonBackground.setBackground(buttonBackgroundGradient);

        GradientDrawable editBorderContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[] {hex2color(dialogStructure.input.border.color),hex2color(dialogStructure.input.border.color)});
        editBorderContainerGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.input.border.radius));

        GradientDrawable editContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[] {hex2color(dialogStructure.input.background.color),hex2color(dialogStructure.input.background.color)});
        editContainerGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.input.border.radius));

        GradientDrawable borderContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[] {hex2color(dialogStructure.border.color),hex2color(dialogStructure.border.color)});
        borderContainerGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.border.radius));

        GradientDrawable contentContainerGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, //set a gradient direction
                new int[] {hex2color(dialogStructure.background.color),hex2color(dialogStructure.background.color)});
        contentContainerGradient.setCornerRadius(Sizes.dpToPxExt(dialogStructure.border.radius));

        editBorderContainer.setBackground(editBorderContainerGradient);
        editContainer.setBackground(editContainerGradient);
        borderContainer.setBackground(borderContainerGradient);
        contentContainer.setBackground(contentContainerGradient);

        editText.addTextChangedListener(new TextWatcher() {
            int lastSpecialRequestsCursorPosition;
            String specialRequests;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastSpecialRequestsCursorPosition = editText.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.isEmpty()) {
                    buttonBackground.setVisibility(View.GONE);
                } else {
                    buttonBackground.setVisibility(View.VISIBLE);
                }

                editText.removeTextChangedListener(this);

                if (editText.getLineCount() > 3) {
                    editText.setText(specialRequests);
                    editText.setSelection(lastSpecialRequestsCursorPosition);
                } else
                    specialRequests = editText.getText().toString();

                editText.addTextChangedListener(this);
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                editText.clearFocus();

                View view = activity.getCurrentFocus();
                if (view != null) {
                    Log.d("closeKeyboard", "close");
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    //  imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        buttonBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sendListener.onSend(id, editText.getEditableText().toString());
            }
        });
        if (!Sizes.isTablet()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }, 200);
        }
    }
}
