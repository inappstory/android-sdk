package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageFullscreenAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.animation.IndependentAnimator;
import com.inappstory.sdk.utils.animation.IndependentAnimatorListener;

public class FullscreenContentContainer extends IAMContentContainer<InAppMessageFullscreenAppearance> {

    FrameLayout content;
    RelativeLayout container;
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
        int backgroundColor = ColorUtils.parseColorRGBA(
                appearance.backgroundColor()
        );
        backgroundColor = Color.argb(0, 255, 255, 255);
        content.setBackgroundColor(backgroundColor);
        generateLoader(backgroundColor);
        container.addView(loaderContainer);
        switch (appearance.closeButtonPosition()) {
            case 0:
                closeButton.setVisibility(GONE);
                break;
            case 1:
                closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                break;
            default:
                closeButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                break;
        }

        closeButton.setLayoutParams(closeButtonLayoutParams);
        closeButton.requestLayout();
        //  showWithAnimation();
    }

    private void setCloseButtonOffset(final Context context) {
        final Rect readerContainer = new Rect();
        closeButton.getGlobalVisibleRect(readerContainer);
        int topOffset = Math.max(readerContainer.top - externalContainerRect.top, 0);
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            if (activity != null && activity.getWindow() != null) {
                if (activity.getWindow().getDecorView().getRootWindowInsets() != null) {
                    DisplayCutout cutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                    if (cutout != null) {
                        closeButtonLayoutParams.topMargin = Math.max(cutout.getSafeInsetTop() - topOffset, 0) +
                                Sizes.dpToPxExt(16, context);
                    } else {
                        int windowTopOffset = activity.getWindow().getDecorView().getRootWindowInsets().getStableInsetTop();
                        closeButtonLayoutParams.topMargin = Math.max(windowTopOffset - topOffset, 0) +
                                Sizes.dpToPxExt(16, context);
                    }
                    closeButton.setLayoutParams(closeButtonLayoutParams);
                }
            }
        }
    }

    @Override
    public void showWithAnimation() {
        if (appearance != null) {
            switch (appearance.animationType()) {
                case 0:
                    setVisibility(VISIBLE);
                    showAnimationEnd();
                    break;
                case 1:
                    background.setAlpha(0f);
                    final float height = getHeight();
                    container.setTranslationY(height);
                    new IndependentAnimator(new IndependentAnimatorListener() {
                        @Override
                        public void onStart() {
                            setVisibility(VISIBLE);
                        }

                        @Override
                        public void onUpdate(final float progress) {
                            container.post(new Runnable() {
                                @Override
                                public void run() {
                                    container.setTranslationY((1f - progress) * height);
                                    background.setAlpha(progress);
                                }
                            });

                        }

                        @Override
                        public void onEnd() {
                            showAnimationEnd();
                        }
                    }).start(
                            200,
                            new DecelerateInterpolator()
                    );
                    break;
                case 2:
                    background.setAlpha(0f);
                    container.setAlpha(0f);
                    container.setScaleX(0.7f);
                    container.setScaleY(0.7f);

                    new IndependentAnimator(new IndependentAnimatorListener() {
                        @Override
                        public void onStart() {
                            setVisibility(VISIBLE);
                        }

                        @Override
                        public void onUpdate(final float progress) {
                            container.post(new Runnable() {
                                @Override
                                public void run() {
                                    container.setScaleX((0.3f * progress) + 0.7f);
                                    container.setScaleY((0.3f * progress) + 0.7f);
                                    container.setAlpha(progress);
                                    background.setAlpha(progress);
                                }
                            });

                        }

                        @Override
                        public void onEnd() {
                            showAnimationEnd();
                        }
                    }).start(
                            200,
                            new DecelerateInterpolator()
                    );
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
                    final float height = getHeight();
                    new IndependentAnimator(new IndependentAnimatorListener() {
                        @Override
                        public void onStart() {
                            setVisibility(VISIBLE);
                        }

                        @Override
                        public void onUpdate(final float progress) {
                            container.post(new Runnable() {
                                @Override
                                public void run() {
                                    container.setTranslationY(progress * height);
                                    background.setAlpha(1f - progress);
                                }
                            });

                        }

                        @Override
                        public void onEnd() {
                            closeAnimationEnd();
                        }
                    }).start(200, new AccelerateInterpolator());
                    break;
                case 2:
                    new IndependentAnimator(new IndependentAnimatorListener() {
                        @Override
                        public void onStart() {
                            setVisibility(VISIBLE);
                        }

                        @Override
                        public void onUpdate(final float progress) {
                            container.post(new Runnable() {
                                @Override
                                public void run() {
                                    container.setScaleX(1f - (0.3f * progress));
                                    container.setScaleY(1f - (0.3f * progress));
                                    container.setAlpha(1f - progress);
                                    background.setAlpha(1f - progress);
                                }
                            });

                        }

                        @Override
                        public void onEnd() {
                            closeAnimationEnd();
                        }
                    }).start(300, new AccelerateInterpolator());
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
        container = new RelativeLayout(context);
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
        showWithAnimation();
    }

    @Override
    protected void visibleRectIsCalculated() {
        setCloseButtonOffset(getContext());
    }

}
