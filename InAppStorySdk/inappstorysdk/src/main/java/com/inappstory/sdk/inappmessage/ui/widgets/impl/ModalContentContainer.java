package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageModalAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContainerCallback;
import com.inappstory.sdk.stories.utils.Sizes;

public class ModalContentContainer extends IAMContentContainer<InAppMessageModalAppearance> {
    private RoundedCornerLayout roundedCornerLayout;
    private FrameLayout content;
    private FrameLayout loaderContainer;
    private FrameLayout.LayoutParams layoutParams;
    private RelativeLayout.LayoutParams closeButtonLayoutParams;
    private ImageView closeButton;

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
        int backgroundColor = ColorUtils.parseColorRGBA(
                appearance.backgroundColor()
        );
        content.setBackgroundColor(backgroundColor);

        double contrast1 = ColorUtils.getColorsContrast(backgroundColor, Color.BLACK);
        double contrast2 = ColorUtils.getColorsContrast(backgroundColor, Color.WHITE);
        loaderContainer = new FrameLayout(getContext());
        loaderContainer.setClickable(true);
        loaderContainer.setBackgroundColor(backgroundColor);
        loaderContainer.setVisibility(GONE);
        loaderContainer.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        View loader = AppearanceManager.getLoader(getContext(), contrast1 > contrast2 ? Color.BLACK : Color.WHITE);
        loaderContainer.addView(loader);
        roundedCornerLayout.addView(loaderContainer);

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
    public void showLoader() {
        loaderContainer.setVisibility(VISIBLE);
        loaderContainer.setAlpha(1f);
    }

    @Override
    public void hideLoader() {
        loaderContainer
                .animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                loaderContainer.setVisibility(GONE);
            }
        });
    }

    public void showWithAnimation() {
        if (appearance != null) {
            switch (appearance.animationType()) {
                case 0:
                    setVisibility(VISIBLE);
                    showAnimationEnd();
                    break;
                case 1:
                    background.setAlpha(0f);
                    roundedCornerLayout.setTranslationY(getHeight());
                    roundedCornerLayout.animate()
                            .translationY(0)
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(500)
                            .setListener(
                                    new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            setVisibility(VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            showAnimationEnd();
                                            //   setVisibility(VISIBLE);
                                        }
                                    }
                            )
                            .start();
                    background.animate().alpha(1f).setDuration(500).start();
                    break;
                case 2:
                    background.setAlpha(0f);
                    roundedCornerLayout.setAlpha(0f);
                    roundedCornerLayout.setScaleX(0.7f);
                    roundedCornerLayout.setScaleY(0.7f);
                    roundedCornerLayout.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setListener(
                                    new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            setVisibility(VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            showAnimationEnd();
                                        }
                                    }
                            )
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(500)
                            .start();
                    background.animate().alpha(1f).setDuration(500).start();
                    break;

            }
        } else {
            setVisibility(VISIBLE);
            showAnimationEnd();
        }
    }

    private void showAnimationEnd() {
        if (callback != null) callback.onShown();
    }

    private void closeAnimationEnd() {
        if (callback != null) callback.onClosed();
    }

    @Override
    public void showWithoutAnimation() {
        setVisibility(VISIBLE);
        showAnimationEnd();
    }

    @Override
    public void closeWithAnimation() {
        if (appearance != null) {
            switch (appearance.animationType()) {
                case 0:
                    closeAnimationEnd();
                    break;
                case 1:
                    roundedCornerLayout.animate()
                            .translationY(getHeight())
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(500)
                            .setListener(
                                    new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            setVisibility(VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            closeAnimationEnd();
                                            //   setVisibility(VISIBLE);
                                        }
                                    }
                            )
                            .start();
                    background.animate().alpha(0f).setDuration(500).start();
                    break;
                case 2:
                    roundedCornerLayout.animate()
                            .scaleX(0.7f)
                            .scaleY(0.7f)
                            .alpha(0f)
                            .setListener(
                                    new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            setVisibility(VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            closeAnimationEnd();
                                        }
                                    }
                            )
                            .setInterpolator(new AccelerateInterpolator())
                            .setDuration(500)
                            .start();
                    background.animate().alpha(0f).setDuration(500).start();
                    break;

            }
        } else {
            closeAnimationEnd();
        }

    }


    @Override
    public void closeWithoutAnimation() {
        closeAnimationEnd();
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
