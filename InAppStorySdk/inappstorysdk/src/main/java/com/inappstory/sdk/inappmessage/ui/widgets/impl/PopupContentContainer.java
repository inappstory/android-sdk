package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.content.Context;
import android.graphics.Point;
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

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.roundedlayout.RoundedCornerLayout;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessagePopupAppearance;
import com.inappstory.sdk.inappmessage.ui.widgets.IAMContentContainer;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.animation.IndependentAnimator;
import com.inappstory.sdk.utils.animation.IndependentAnimatorListener;

public class PopupContentContainer extends IAMContentContainer<InAppMessagePopupAppearance> {
    private RoundedCornerLayout roundedCornerLayout;
    private FrameLayout content;
    private FrameLayout.LayoutParams layoutParams;
    private RelativeLayout.LayoutParams closeButtonLayoutParams;
    private ImageView closeButton;

    public PopupContentContainer(
            @NonNull Context context
    ) {
        super(context);
    }

    public PopupContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
    }

    public PopupContentContainer(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void appearance(InAppMessagePopupAppearance appearance) {
        super.appearance(appearance);
        if (content == null) return;
        int backgroundColor = ColorUtils.parseColorRGBA(
                appearance.backgroundColor()
        );
        content.setBackgroundColor(backgroundColor);
        generateLoader(backgroundColor);
        roundedCornerLayout.addView(loaderContainer);


        int horizontalPadding = Sizes.dpToPxExt(appearance.horizontalPadding(), getContext());
        layoutParams.leftMargin = horizontalPadding;
        layoutParams.rightMargin = horizontalPadding;
        if (appearance.contentRatio() == 0)
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        else {
            Point size = Sizes.getScreenSize(getContext());
            layoutParams.height = Math.min(size.y, Math.round((size.x - 2 * horizontalPadding) * appearance.contentRatio()));
        }
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

    public void showWithAnimation() {
        if (appearance != null) {
            switch (appearance.animationType()) {
                case 0:
                    setVisibility(VISIBLE);
                    showAnimationEnd();
                    break;
                case 1:
                    background.setAlpha(0f);
                    final int height = getHeight();
                    roundedCornerLayout.setTranslationY(height);
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
                                    roundedCornerLayout.setTranslationY((1f - progress) * height);
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
                case 2:
                    background.setAlpha(0f);
                    roundedCornerLayout.setAlpha(0f);
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
                                    roundedCornerLayout.setAlpha(progress);
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
                    final float height = getHeight();
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
                                    roundedCornerLayout.setTranslationY(progress * height);
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
                            roundedCornerLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    roundedCornerLayout.setScaleX(1f - (0.3f * progress));
                                    roundedCornerLayout.setScaleY(1f - (0.3f * progress));
                                    roundedCornerLayout.setAlpha(1f - progress);
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