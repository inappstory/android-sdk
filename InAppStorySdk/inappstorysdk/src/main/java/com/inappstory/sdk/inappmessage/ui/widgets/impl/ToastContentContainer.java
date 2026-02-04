package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SizeF;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithoutStates;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageToastAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.animation.IndependentAnimator;
import com.inappstory.sdk.utils.animation.IndependentAnimatorListener;

public class ToastContentContainer extends IAMContentContainer<InAppMessageToastAppearance> {
    private FrameLayout mainLayout;
    private RoundedCornerLayout roundedCornerLayout;
    private LayoutParams layoutParams;
    private LayoutParams closeButtonLayoutParams;
    private TouchFrameLayout closeButton;

    public ToastContentContainer(
            @NonNull Context context
    ) {
        super(context);
    }

    public ToastContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public ToastContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    int maxViewHeight = 0;

    @Override
    public void appearance(InAppMessageToastAppearance appearance) {
        super.appearance(appearance);
        if (content == null) return;
        background.setBackgroundColor(Color.TRANSPARENT);
        background.setClickable(false);
        int backgroundColor = ColorUtils.parseColorRGBA(
                appearance.backgroundColor()
        );
        content.setBackground(appearance.backgroundDrawable());
        generateLoader(backgroundColor);
        generateRefresh();
        roundedCornerLayout.addView(loaderContainer);
        roundedCornerLayout.addView(refreshContainer);
        if (closeEnabled) {
            switch (appearance.closeButtonPosition()) {
                case 0:
                    closeButton.setVisibility(GONE);
                    break;
                case 1:
                    closeButtonLayoutParams.gravity = Gravity.START;
                    break;
                default:
                    closeButtonLayoutParams.gravity = Gravity.END;
                    break;
            }
        } else {
            closeButton.setVisibility(GONE);
        }

        //
        closeButton.setLayoutParams(closeButtonLayoutParams);
        closeButton.requestLayout();
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
                    final int height = maxViewHeight;
                    if (appearance.verticalPosition() == 0) {
                        mainLayout.setTranslationY(height);
                        new IndependentAnimator(new IndependentAnimatorListener() {
                            @Override
                            public void onStart() {
                                setVisibility(VISIBLE);
                            }

                            @Override
                            public void onUpdate(final float progress) {
                                mainLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainLayout.setTranslationY((1f - progress) * height);
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
                                new AccelerateInterpolator()
                        );
                    } else {
                        mainLayout.setTranslationY(-height);
                        new IndependentAnimator(new IndependentAnimatorListener() {
                            @Override
                            public void onStart() {
                                setVisibility(VISIBLE);
                            }

                            @Override
                            public void onUpdate(final float progress) {
                                mainLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainLayout.setTranslationY((1f - progress) * -height);
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
                                new AccelerateInterpolator()
                        );
                    }
                    break;
                case 2:
                    background.setAlpha(0f);
                    mainLayout.setAlpha(0f);
                    roundedCornerLayout.setScaleX(0.7f);
                    roundedCornerLayout.setScaleY(0.7f);
                    new IndependentAnimator(new IndependentAnimatorListener() {
                        @Override
                        public void onStart() {
                            setVisibility(VISIBLE);
                        }

                        @Override
                        public void onUpdate(final float progress) {
                            roundedCornerLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    roundedCornerLayout.setScaleX((0.3f * progress) + 0.7f);
                                    roundedCornerLayout.setScaleY((0.3f * progress) + 0.7f);
                                    mainLayout.setAlpha(progress);
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
                            new AccelerateInterpolator()
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

                    final float height = maxViewHeight;
                    if (appearance.verticalPosition() == 0) {
                        new IndependentAnimator(new IndependentAnimatorListener() {
                            @Override
                            public void onStart() {
                                setVisibility(VISIBLE);
                            }

                            @Override
                            public void onUpdate(final float progress) {
                                roundedCornerLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainLayout.setTranslationY(progress * height);
                                        background.setAlpha(1f - progress);
                                    }
                                });

                            }

                            @Override
                            public void onEnd() {
                                closeAnimationEnd();
                            }
                        }).start(200, new AccelerateInterpolator());
                    } else {
                        new IndependentAnimator(new IndependentAnimatorListener() {
                            @Override
                            public void onStart() {
                                setVisibility(VISIBLE);
                            }

                            @Override
                            public void onUpdate(final float progress) {
                                mainLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainLayout.setTranslationY(progress * -height);
                                        background.setAlpha(1f - progress);
                                    }
                                });

                            }

                            @Override
                            public void onEnd() {
                                closeAnimationEnd();
                            }
                        }).start(200, new AccelerateInterpolator());
                    }

                    break;
                case 2:
                    new IndependentAnimator(new IndependentAnimatorListener() {
                        @Override
                        public void onStart() {
                            setVisibility(VISIBLE);
                        }

                        @Override
                        public void onUpdate(final float progress) {
                            mainLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    roundedCornerLayout.setScaleX(1f - (0.3f * progress));
                                    roundedCornerLayout.setScaleY(1f - (0.3f * progress));
                                    mainLayout.setAlpha(1f - progress);
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

            }
        } else {
            closeAnimationEnd();
        }

    }

    @Override
    protected int visibleRectIsCalculated() {
        int horizontalOffset = Sizes.dpToPxExt(appearance.horizontalOffset(), getContext());
        int verticalOffset = Sizes.dpToPxExt(appearance.verticalOffset(), getContext());
        layoutParams.leftMargin = horizontalOffset;
        layoutParams.rightMargin = horizontalOffset;
        layoutParams.topMargin = verticalOffset;
        layoutParams.bottomMargin = verticalOffset;
        float contentRatio = 6.4f;
        if (appearance.contentRatio() < 30f && appearance.contentRatio() > 0.01f) {
            contentRatio = appearance.contentRatio();
        }
        if (appearance.verticalPosition() == 1) {
            layoutParams.gravity = Gravity.TOP;
        }
        switch (appearance.horizontalPosition()) {
            case 0:
                layoutParams.gravity |= Gravity.START;
                break;
            case 2:
                layoutParams.gravity |= Gravity.END;
                break;
            default:
                layoutParams.gravity |= Gravity.CENTER_HORIZONTAL;
                break;
        }
        float availableWidth = externalContainerRect.width() - 2 * horizontalOffset;
        Point screenSize = Sizes.getScreenSize(getContext());
        if (Sizes.isTablet(getContext())) {
            availableWidth = Math.min(availableWidth, Sizes.dpToPxExt(320, getContext()));
        } else {
            availableWidth = Math.min(
                    availableWidth,
                    Math.min(screenSize.x, screenSize.y) - 2 * horizontalOffset
            );
        }
        float availableHeight = externalContainerRect.height() - verticalOffset;
        float screenContentRatio = availableWidth / availableHeight;

        int height = 0;
        if (contentRatio >= screenContentRatio) {
            height = Math.min(
                    Math.round(availableHeight),
                    Math.round(availableWidth / contentRatio)
            );
            if (Sizes.isTablet(getContext())) {
                layoutParams.width = Math.round(availableWidth);
            }
        } else {
            height = Math.round(availableHeight);
            layoutParams.width = Math.round(availableHeight * contentRatio);
        }
        layoutParams.height = height;
        maxViewHeight = height + verticalOffset;
        int closeButtonMargin = Math.min(
                (height - Sizes.dpToPxExt(30, getContext())) / 2,
                Sizes.dpToPxExt(16, getContext())
        );
        closeButtonLayoutParams.setMargins(
                closeButtonMargin,
                closeButtonMargin,
                closeButtonMargin,
                closeButtonMargin
        );
        roundedCornerLayout.setRadius(
                Sizes.dpToPxExt(appearance.cornerRadius(), getContext())
        );
        mainLayout.setLayoutParams(
                layoutParams
        );
        mainLayout.requestLayout();
        return height;
    }

    @Override
    protected Pair<Integer, Integer> countSafeArea(int containerHeight) {
        Rect mainLayoutRect = new Rect();
        mainLayout.getGlobalVisibleRect(mainLayoutRect);
        int topOffset = Math.max(mainLayoutRect.top, 0);
        int bottomOffset = Math.max(mainLayoutRect.bottom, 0);
        Context context = getContext();
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        if (activity == null) return new Pair<>(0, 0);
        int phoneHeight = Sizes.getFullPhoneHeight(activity);
        int topInsetOffset = 0;
        int bottomInsetOffset = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.getWindow() != null) {
                WindowInsets windowInsets = activity.getWindow().getDecorView().getRootWindowInsets();
                if (windowInsets != null) {
                    topInsetOffset = Math.max(0, windowInsets.getStableInsetTop());
                    bottomInsetOffset = Math.max(0, windowInsets.getStableInsetBottom());
                }
            }
        }
        return new Pair<>(
                Math.max(0, topInsetOffset - topOffset),
                Math.max(0, bottomInsetOffset - (phoneHeight - bottomOffset))
        );
    }

    @Override
    public void closeWithoutAnimation() {
        closeAnimationEnd();
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mainLayout = new FrameLayout(context);
        roundedCornerLayout = new RoundedCornerLayout(context);
        roundedCornerLayout.setClickable(true);
        LayoutParams frameLayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        layoutParams.gravity = Gravity.BOTTOM;
        roundedCornerLayout.setLayoutParams(frameLayoutParams);
        mainLayout.setLayoutParams(layoutParams);
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
        content.setVisibility(GONE);
        closeButton = new TouchFrameLayout(context);
        int maxSize = Sizes.dpToPxExt(30, context);
        closeButtonLayoutParams = new LayoutParams(
                maxSize,
                maxSize
        );
        closeButton.setElevation(8f);
        final CustomIconWithoutStates closeIconInterface = AppearanceManager.getCommonInstance().csCustomIcons().closeIcon();
        final View closeView = closeIconInterface.createIconView(context, new SizeF(maxSize, maxSize));
        closeView.setClickable(false);
        closeButton.addView(closeView);
        closeButton.setTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeIconInterface.touchEvent(closeView, event);
                return false;
            }
        });
        closeButton.setClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    closeIconInterface.clickEvent(closeView);
                } catch (Exception e) {

                }
                closeWithAnimation();
            }
        });
        int closeButtonMargin = Sizes.dpToPxExt(16, context);
        closeButtonLayoutParams.setMargins(
                closeButtonMargin,
                closeButtonMargin,
                closeButtonMargin,
                closeButtonMargin
        );
        closeButton.setLayoutParams(closeButtonLayoutParams);
        relativeLayout.addView(content);
        content.setId(CONTENT_ID);
        mainLayout.addView(roundedCornerLayout);
        roundedCornerLayout.addView(relativeLayout);
        roundedCornerLayout.addView(closeButton);
        addView(mainLayout);
        if (appearance != null) appearance(appearance);
    }
}
