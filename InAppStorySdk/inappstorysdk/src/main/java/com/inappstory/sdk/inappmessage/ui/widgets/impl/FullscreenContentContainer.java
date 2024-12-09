package com.inappstory.sdk.inappmessage.ui.widgets.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
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
        content.setBackgroundColor(backgroundColor);
        generateLoader(backgroundColor);
        container.addView(loaderContainer);
        closeButtonLayoutParams.addRule(
                appearance.closeButtonPosition() == 1 ?
                        RelativeLayout.ALIGN_PARENT_START :
                        RelativeLayout.ALIGN_PARENT_END
        );
        closeButton.setLayoutParams(closeButtonLayoutParams);
        closeButton.requestLayout();
        //  showWithAnimation();
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

}
