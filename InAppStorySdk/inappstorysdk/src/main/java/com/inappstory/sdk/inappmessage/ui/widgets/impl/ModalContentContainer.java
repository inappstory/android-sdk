package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;
import com.inappstory.sdk.stories.utils.Sizes;

public class ModalContentContainer extends IAMContentContainer<InAppMessageModalAppearance> {
    RoundedCornerLayout roundedCornerLayout;
    FrameLayout content;
    FrameLayout.LayoutParams layoutParams;
    RelativeLayout.LayoutParams closeButtonLayoutParams;
    ImageView closeButton;

    public ModalContentContainer(
            @NonNull Context context
    ) {
        super(context);
    }

    public ModalContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public ModalContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void appearance(InAppMessageModalAppearance appearance) {
        super.appearance(appearance);
        if (content == null) return;
        content.setBackgroundColor(ColorUtils.parseColorRGBA(
                appearance.backgroundColor()
        ));
        if (appearance.contentHeight() == -1) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            layoutParams.height = Sizes.dpToPxExt(appearance.contentHeight(), getContext());
        }

        int horizontalPadding = Sizes.dpToPxExt(appearance.horizontalPadding(), getContext());
        layoutParams.leftMargin = horizontalPadding;
        layoutParams.rightMargin = horizontalPadding;
        roundedCornerLayout.setRadius(
                Sizes.dpToPxExt(appearance.cornerRadius(), getContext())
        );
        roundedCornerLayout.setLayoutParams(
                layoutParams
        );
        closeButtonLayoutParams.addRule(
                appearance.closeButtonPosition() == 1 ?
                        RelativeLayout.ALIGN_PARENT_START :
                        RelativeLayout.ALIGN_PARENT_END
        );
        closeButton.setLayoutParams(closeButtonLayoutParams);
        roundedCornerLayout.requestLayout();
        closeButton.requestLayout();
    }


    @Override
    public void showWithAnimation() {
        if (appearance != null) {
            if (callback != null) callback.onShown();
        }
    }

    @Override
    public void showWithoutAnimation() {
        if (callback != null) callback.onShown();
    }

    @Override
    public void closeWithAnimation() {
        if (appearance != null) {
            if (callback != null) callback.onClosed();
        }
    }

    @Override
    public void closeWithoutAnimation() {
        if (callback != null) callback.onClosed();
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        roundedCornerLayout = new RoundedCornerLayout(context);
        roundedCornerLayout.setClickable(true);
        layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.gravity = Gravity.CENTER;
        roundedCornerLayout.setLayoutParams(layoutParams);
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        content = new FrameLayout(context);
        content.setLayoutParams(
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        closeButton = new ImageView(context);
        closeButtonLayoutParams = new RelativeLayout.LayoutParams(
                Sizes.dpToPxExt(32, context),
                Sizes.dpToPxExt(32, context)
        );
        closeButton.setElevation(8f);
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWithAnimation();
            }
        });
        closeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stories_close));
        int closeButtonMargin = Sizes.dpToPxExt(16, context);
        closeButtonLayoutParams.setMargins(
                closeButtonMargin,
                closeButtonMargin,
                closeButtonMargin,
                closeButtonMargin
        );
        closeButton.setLayoutParams(closeButtonLayoutParams);
        relativeLayout.addView(content);
        relativeLayout.addView(closeButton);
        content.setId(CONTENT_ID);
        roundedCornerLayout.addView(relativeLayout);
        addView(roundedCornerLayout);
        if (appearance != null) appearance(appearance);
    }
}
