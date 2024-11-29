package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;
import com.inappstory.sdk.stories.utils.Sizes;

public class FullscreenContentContainer extends IAMContentContainer<InAppMessageFullscreenAppearance> {

    FrameLayout content;
    RelativeLayout.LayoutParams closeButtonLayoutParams;
    ImageView closeButton;

    public FullscreenContentContainer(
            @NonNull Context context
    ) {
        super(context);
    }

    public FullscreenContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public FullscreenContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void appearance(InAppMessageFullscreenAppearance appearance) {
        super.appearance(appearance);
        if (content == null) return;
        content.setBackgroundColor(ColorUtils.parseColorRGBA(
                appearance.backgroundColor()
        ));
        closeButtonLayoutParams.addRule(
                appearance.closeButtonPosition() == 1 ?
                        RelativeLayout.ALIGN_PARENT_START :
                        RelativeLayout.ALIGN_PARENT_END
        );
        closeButton.setLayoutParams(closeButtonLayoutParams);
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
        RelativeLayout container = new RelativeLayout(context);
        container.setClickable(true);
        container.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        addView(container);
        content = new FrameLayout(context);
        content.setLayoutParams(
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        content.setId(CONTENT_ID);
        container.addView(content);
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
        container.addView(closeButton);
        if (appearance != null) appearance(appearance);
    }

}
