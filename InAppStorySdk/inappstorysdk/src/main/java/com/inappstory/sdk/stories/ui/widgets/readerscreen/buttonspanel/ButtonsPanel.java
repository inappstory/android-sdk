package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;

public class ButtonsPanel extends LinearLayout {

    public AppCompatImageView like;
    public AppCompatImageView sound;
    public AppCompatImageView dislike;
    public AppCompatImageView favorite;
    public AppCompatImageView share;

    private int storyId;

    public ButtonsPanel(Context context, int storyId) {
        super(context);
        this.storyId = storyId;
        init();
    }

    public ButtonsPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonsPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setButtonsStatus(int likeVal, int favVal) {
        if (like != null)
            like.setActivated(likeVal == 1);
        if (dislike != null)
            dislike.setActivated(likeVal == -1);
        if (favorite != null)
            favorite.setActivated(favVal == 1);
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
        like.setVisibility(hasLike ? VISIBLE : GONE);
        dislike.setVisibility(hasLike ? VISIBLE : GONE);
        favorite.setVisibility(hasFavorite ? VISIBLE : GONE);
        share.setVisibility(hasShare ? VISIBLE : GONE);
        sound.setVisibility(hasSound ? VISIBLE : GONE);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                sound.setActivated(
                        ((IASDataSettingsHolder) core.settingsAPI()).isSoundOn()
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
        sound.setActivated(((IASDataSettingsHolder) core.settingsAPI()).isSoundOn());
    }

    ButtonsPanelManager manager;

    public void init() {
        inflate(getContext(), R.layout.cs_buttons_panel, this);
        like = findViewById(R.id.likeButton);
        dislike = findViewById(R.id.dislikeButton);
        favorite = findViewById(R.id.favoriteButton);
        sound = findViewById(R.id.soundButton);
        share = findViewById(R.id.shareButton);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                manager = new ButtonsPanelManager(ButtonsPanel.this, core);
                if (like != null)
                    like.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            likeClick();
                        }
                    });
                if (dislike != null)
                    dislike.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dislikeClick();
                        }
                    });
                if (favorite != null)
                    favorite.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favoriteClick();
                        }
                    });
                if (share != null)
                    share.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareClick();
                        }
                    });
                if (sound != null) {
                    sound.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            soundClick();
                        }
                    });
                    sound.setActivated(((IASDataSettingsHolder) core.settingsAPI()).isSoundOn());
                }
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setIcons(LaunchStoryScreenAppearance readerSettings) {
        like.setImageDrawable(getResources().getDrawable(readerSettings.csLikeIcon()));
        dislike.setImageDrawable(getResources().getDrawable(readerSettings.csDislikeIcon()));
        favorite.setImageDrawable(getResources().getDrawable(readerSettings.csFavoriteIcon()));
        share.setImageDrawable(getResources().getDrawable(readerSettings.csShareIcon()));
        sound.setImageDrawable(getResources().getDrawable(readerSettings.csSoundIcon()));
    }

    public void likeClick() {
        like.setEnabled(false);
        like.setClickable(false);
        manager.likeClick(new ButtonClickCallback() {
            @Override
            public void onSuccess(final int val) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        like.setEnabled(true);
                        like.setClickable(true);
                        like.setActivated(val == 1);
                        dislike.setActivated(val == -1);
                    }
                });
            }

            @Override
            public void onError() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        like.setEnabled(true);
                        like.setClickable(true);
                    }
                });
            }
        });
    }

    public void dislikeClick() {
        dislike.setEnabled(false);
        dislike.setClickable(false);
        manager.dislikeClick(new ButtonClickCallback() {
            @Override
            public void onSuccess(final int val) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        dislike.setEnabled(true);
                        dislike.setClickable(true);
                        like.setActivated(val == 1);
                        dislike.setActivated(val == -1);
                    }
                });
            }

            @Override
            public void onError() {

                post(new Runnable() {
                    @Override
                    public void run() {
                        dislike.setEnabled(true);
                        dislike.setClickable(true);
                    }
                });
            }
        });
    }

    public void forceRemoveFromFavorite() {

        post(new Runnable() {
            @Override
            public void run() {
                if (favorite != null) {
                    favorite.setEnabled(true);
                    favorite.setClickable(true);
                    favorite.setActivated(false);
                }
            }
        });
    }

    public void favoriteClick() {
        favorite.setEnabled(false);
        favorite.setClickable(false);
        manager.favoriteClick(new ButtonClickCallback() {
            @Override
            public void onSuccess(final int val) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        favorite.setEnabled(true);
                        favorite.setClickable(true);
                        favorite.setActivated(val == 1);
                    }
                });
            }

            @Override
            public void onError() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        favorite.setEnabled(true);
                        favorite.setClickable(true);
                    }
                });
            }
        });
    }

    public void soundClick() {
        manager.soundClick();
    }

    public void shareClick() {
        share.setEnabled(false);
        share.setClickable(false);
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
                        share.setEnabled(true);
                        share.setClickable(true);
                    }
                });
            }
        });
    }
}
