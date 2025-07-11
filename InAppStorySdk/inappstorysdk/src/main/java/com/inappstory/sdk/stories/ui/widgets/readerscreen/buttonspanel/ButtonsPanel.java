package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.ICustomAppearanceIcons;
import com.inappstory.sdk.ICustomIcon;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconState;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;

public class ButtonsPanel extends LinearLayout {

    private View like;
    private View sound;
    private View dislike;
    private View favorite;
    private View share;

    private ICustomIcon likeInterface;
    private ICustomIcon soundInterface;
    private ICustomIcon dislikeInterface;
    private ICustomIcon favoriteInterface;
    private ICustomIcon shareInterface;

    private TouchFrameLayout likeLayout;
    private TouchFrameLayout dislikeLayout;
    private TouchFrameLayout soundLayout;
    private TouchFrameLayout favoriteLayout;
    private TouchFrameLayout shareLayout;

    private boolean likeActive = false;
    private boolean dislikeActive = false;
    private boolean favoriteActive = false;

    public ButtonsPanel(Context context, int storyId) {
        super(context);
        init(context);
    }

    public ButtonsPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ButtonsPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void unlockShareButton() {
        if (shareLayout != null) {
            shareLayout.post(new Runnable() {
                @Override
                public void run() {
                    shareLayout.setClickable(true);
                    shareInterface.updateState(share, new CustomIconState(true, true));
                }
            });
        }
    }

    public void setButtonsStatus(int likeVal, int favVal) {
        if (likeLayout != null)
            likeInterface.updateState(like, new CustomIconState(likeVal == 1, true));
        if (dislikeLayout != null)
            dislikeInterface.updateState(dislike, new CustomIconState(likeVal == -1, true));
        if (favoriteLayout != null)
            favoriteInterface.updateState(favorite, new CustomIconState(favVal == 1, true));
    }

    public boolean panelIsVisible() {
        return isVisible;
    }

    private boolean isVisible = true;

    public void setButtonsVisibility(
            LaunchStoryScreenAppearance readerSettings,
            boolean hasLike,
            boolean hasFavorite,
            boolean hasShare,
            boolean hasSound,
            boolean isTablet
    ) {
        hasLike = hasLike && readerSettings.csHasLike();
        hasFavorite = hasFavorite && readerSettings.csHasFavorite();
        hasShare = hasShare && readerSettings.csHasShare();
        likeLayout.setVisibility(hasLike ? VISIBLE : GONE);
        dislikeLayout.setVisibility(hasLike ? VISIBLE : GONE);
        favoriteLayout.setVisibility(hasFavorite ? VISIBLE : GONE);
        shareLayout.setVisibility(hasShare ? VISIBLE : GONE);
        soundLayout.setVisibility(hasSound ? VISIBLE : GONE);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                soundInterface.updateState(
                        sound,
                        new CustomIconState(
                                ((IASDataSettingsHolder) core.settingsAPI()).isSoundOn(),
                                true
                        )
                );
            }
        });
        this.isVisible = (hasFavorite || hasLike || hasShare || hasSound);
        if (!isVisible && isTablet) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void setVisibility(int visibility) {

        super.setVisibility(visibility);
    }

    public ButtonsPanelManager getManager() {
        return manager;
    }


    public void refreshSoundStatus(IASCore core) {
        soundInterface.updateState(
                sound,
                new CustomIconState(
                        ((IASDataSettingsHolder) core.settingsAPI()).isSoundOn(),
                        true
                )
        );
    }

    ButtonsPanelManager manager;

    public void init(Context context) {
        inflate(getContext(), R.layout.cs_buttons_panel_layout, this);
        likeLayout = findViewById(R.id.likeButton);
        dislikeLayout = findViewById(R.id.dislikeButton);
        favoriteLayout = findViewById(R.id.favoriteButton);
        soundLayout = findViewById(R.id.soundButton);
        shareLayout = findViewById(R.id.shareButton);
        ICustomAppearanceIcons customAppearanceIcons = AppearanceManager.getCommonInstance().csCustomIcons();
        SizeF sizeF = new SizeF(Sizes.dpToPxExt(30, context), Sizes.dpToPxExt(30, context));
        likeInterface = customAppearanceIcons.likeIcon();
        dislikeInterface = customAppearanceIcons.dislikeIcon();
        favoriteInterface = customAppearanceIcons.favoriteIcon();
        soundInterface = customAppearanceIcons.soundIcon();
        shareInterface = customAppearanceIcons.shareIcon();
        like = likeInterface.createIconView(context, sizeF);
        dislike = dislikeInterface.createIconView(context, sizeF);
        favorite = favoriteInterface.createIconView(context, sizeF);
        sound = soundInterface.createIconView(context, sizeF);
        share = shareInterface.createIconView(context, sizeF);
        like.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dislike.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        favorite.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        sound.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        share.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        likeLayout.addView(like);
        dislikeLayout.addView(dislike);
        shareLayout.addView(share);
        soundLayout.addView(sound);
        favoriteLayout.addView(favorite);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                manager = new ButtonsPanelManager(ButtonsPanel.this, core);
                if (likeLayout != null) {
                    likeLayout.setClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            likeClick();
                            try {
                                likeInterface.clickEvent(like);
                            } catch (Exception e) {

                            }
                        }
                    });
                    likeLayout.setTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            likeInterface.touchEvent(like, event);
                            return false;
                        }
                    });
                }
                if (dislikeLayout != null) {
                    dislikeLayout.setClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dislikeClick();
                            try {
                                dislikeInterface.clickEvent(dislike);
                            } catch (Exception e) {

                            }
                        }
                    });
                    dislikeLayout.setTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            dislikeInterface.touchEvent(dislike, event);
                            return false;
                        }
                    });
                }
                if (favoriteLayout != null) {
                    favoriteLayout.setClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favoriteClick();
                            try {
                                favoriteInterface.clickEvent(favorite);
                            } catch (Exception e) {

                            }
                        }
                    });
                    favoriteLayout.setTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            favoriteInterface.touchEvent(favorite, event);
                            return false;
                        }
                    });
                }
                if (shareLayout != null) {
                    shareLayout.setClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareClick();
                            try {
                                shareInterface.clickEvent(share);
                            } catch (Exception e) {

                            }
                        }
                    });
                    shareLayout.setTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            shareInterface.touchEvent(share, event);
                            return false;
                        }
                    });
                }
                if (soundLayout != null) {
                    soundLayout.setClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            soundClick();
                            try {
                                soundInterface.clickEvent(sound);
                            } catch (Exception e) {

                            }
                        }
                    });
                    soundLayout.setTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            soundInterface.touchEvent(sound, event);
                            return false;
                        }
                    });
                    soundInterface.updateState(
                            sound,
                            new CustomIconState(
                                    ((IASDataSettingsHolder) core.settingsAPI()).isSoundOn(),
                                    true
                            )
                    );
                }
            }
        });
    }

   /* @SuppressLint("UseCompatLoadingForDrawables")
    public void setIcons(LaunchStoryScreenAppearance readerSettings) {
        like.setImageDrawable(getResources().getDrawable(readerSettings.csLikeIcon()));
        dislike.setImageDrawable(getResources().getDrawable(readerSettings.csDislikeIcon()));
        favorite.setImageDrawable(getResources().getDrawable(readerSettings.csFavoriteIcon()));
        share.setImageDrawable(getResources().getDrawable(readerSettings.csShareIcon()));
        sound.setImageDrawable(getResources().getDrawable(readerSettings.csSoundIcon()));
    }*/

    public void likeClick() {
        likeInterface.updateState(like, new CustomIconState(likeActive, false));
        dislikeInterface.updateState(dislike, new CustomIconState(dislikeActive, false));
        likeLayout.setClickable(false);
        dislikeLayout.setClickable(false);
        manager.likeClick(new ButtonClickCallback() {
            @Override
            public void onSuccess(final int val) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        likeActive = val == 1;
                        dislikeActive = val == -1;
                        likeInterface.updateState(like, new CustomIconState(likeActive, true));
                        dislikeInterface.updateState(dislike, new CustomIconState(dislikeActive, true));
                        likeLayout.setClickable(true);
                        dislikeLayout.setClickable(true);
                    }
                });
            }

            @Override
            public void onError() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        likeInterface.updateState(like, new CustomIconState(likeActive, true));
                        dislikeInterface.updateState(dislike, new CustomIconState(dislikeActive, true));
                        likeLayout.setClickable(true);
                        dislikeLayout.setClickable(true);
                    }
                });
            }
        });
    }

    public void dislikeClick() {
        likeInterface.updateState(like, new CustomIconState(likeActive, false));
        dislikeInterface.updateState(dislike, new CustomIconState(dislikeActive, false));
        likeLayout.setClickable(false);
        dislikeLayout.setClickable(false);
        manager.dislikeClick(new ButtonClickCallback() {
            @Override
            public void onSuccess(final int val) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        likeActive = val == 1;
                        dislikeActive = val == -1;
                        likeInterface.updateState(like, new CustomIconState(likeActive, true));
                        dislikeInterface.updateState(dislike, new CustomIconState(dislikeActive, true));
                        likeLayout.setClickable(true);
                        dislikeLayout.setClickable(true);
                    }
                });
            }

            @Override
            public void onError() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        likeInterface.updateState(like, new CustomIconState(likeActive, true));
                        dislikeInterface.updateState(dislike, new CustomIconState(dislikeActive, true));
                        likeLayout.setClickable(true);
                        dislikeLayout.setClickable(true);
                    }
                });
            }
        });
    }

    public void forceRemoveFromFavorite() {
        post(new Runnable() {
            @Override
            public void run() {
                if (favoriteLayout != null) {
                    favoriteActive = false;
                    favoriteInterface.updateState(favorite, new CustomIconState(true, false));
                    favoriteLayout.setClickable(true);
                }
            }
        });
    }

    public void favoriteClick() {
        favoriteInterface.updateState(favorite, new CustomIconState(favoriteActive, false));
        favoriteLayout.setClickable(false);
        manager.favoriteClick(new ButtonClickCallback() {
            @Override
            public void onSuccess(final int val) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        favoriteActive = val == 1;
                        favoriteLayout.setClickable(true);
                        favoriteInterface.updateState(favorite, new CustomIconState(favoriteActive, true));
                    }
                });
            }

            @Override
            public void onError() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        favoriteLayout.setClickable(true);
                        favoriteInterface.updateState(favorite, new CustomIconState(favoriteActive, true));
                    }
                });
            }
        });
    }

    public void soundClick() {
        manager.soundClick();
    }

    public void shareClick() {
        shareInterface.updateState(share, new CustomIconState(true, false));
        shareLayout.setClickable(false);
        manager.shareClick(new ButtonsPanelManager.ShareButtonClickCallback() {
            @Override
            void onClick() {
                manager.getPageManager().pauseSlide(false);
            }

            @Override
            public void onSuccess(int val) {

            }

            @Override
            public void onError() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        shareInterface.updateState(share, new CustomIconState(true, true));
                        shareLayout.setClickable(true);
                    }
                });
            }
        });
    }
}
