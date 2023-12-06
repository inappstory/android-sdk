package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.interfaces.IChangeStatusReaderCallback;
import com.inappstory.sdk.stories.ui.oldreader.StoriesReaderSettings;

public class ButtonsPanel extends LinearLayout {

    public AppCompatImageView like;
    public AppCompatImageView sound;
    public AppCompatImageView dislike;
    public AppCompatImageView favorite;
    public AppCompatImageView share;


    public ButtonsPanel(Context context) {
        super(context);
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

    public void setButtonsVisibility(StoriesReaderSettings readerSettings, boolean hasLike, boolean hasFavorite, boolean hasShare, boolean hasSound) {
        hasLike = hasLike && readerSettings.hasLike;
        hasFavorite = hasFavorite && readerSettings.hasFavorite;
        hasShare = hasShare && readerSettings.hasShare;
        like.setVisibility(hasLike ? VISIBLE : GONE);
        dislike.setVisibility(hasLike ? VISIBLE : GONE);
        favorite.setVisibility(hasFavorite ? VISIBLE : GONE);
        share.setVisibility(hasShare ? VISIBLE : GONE);
        sound.setVisibility(hasSound ? VISIBLE : GONE);
        sound.setActivated(IASCore.getInstance().isSoundOn());
        if (hasFavorite || hasLike || hasShare || hasSound) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    public ButtonsPanelManager getManager() {
        return manager;
    }


    public void refreshSoundStatus() {
        sound.setActivated(IASCore.getInstance().isSoundOn());
    }

    ButtonsPanelManager manager;

    public void init() {
        inflate(getContext(), R.layout.ias_reader_buttons_panel, this);
        manager = new ButtonsPanelManager(this);
        like = findViewById(R.id.likeButton);
        dislike = findViewById(R.id.dislikeButton);
        favorite = findViewById(R.id.favoriteButton);
        sound = findViewById(R.id.soundButton);
        share = findViewById(R.id.shareButton);
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
            sound.setActivated(IASCore.getInstance().isSoundOn());
        }
    }

    public void setIcons(StoriesReaderSettings readerSettings) {
        like.setImageDrawable(getResources().getDrawable(readerSettings.likeIcon));
        dislike.setImageDrawable(getResources().getDrawable(readerSettings.dislikeIcon));
        favorite.setImageDrawable(getResources().getDrawable(readerSettings.favoriteIcon));
        share.setImageDrawable(getResources().getDrawable(readerSettings.shareIcon));
        sound.setImageDrawable(getResources().getDrawable(readerSettings.soundIcon));
    }


    private IChangeStatusReaderCallback likeDislikeCallback = new IChangeStatusReaderCallback() {
        @Override
        public void onProcess() {
            post(new Runnable() {
                @Override
                public void run() {
                    like.setEnabled(false);
                    dislike.setEnabled(false);
                    like.setClickable(false);
                    dislike.setClickable(false);
                }
            });
        }

        @Override
        public void onSuccess(final int val) {
            post(new Runnable() {
                @Override
                public void run() {
                    like.setEnabled(true);
                    like.setClickable(true);
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
                    like.setEnabled(true);
                    like.setClickable(true);
                    dislike.setEnabled(true);
                    dislike.setClickable(true);
                }
            });
        }
    };

    public void likeClick() {
        manager.likeClick();
    }

    public void dislikeClick() {
        manager.dislikeClick();
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
        manager.favoriteClick();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    public void subscribe() {
        IStoriesRepository repository = IASCore.getInstance()
                .getStoriesRepository(manager.getParentManager().getStoryType());
        repository.addReaderStatusChangeCallbacks(
                likeDislikeCallback,
                favoriteCallback,
                shareCallback,
                manager.storyId
        );
    }

    public void unsubscribe() {
        IStoriesRepository repository = IASCore.getInstance()
                .getStoriesRepository(manager.getParentManager().getStoryType());
        repository.removeReaderStatusChangeCallbacks(
                likeDislikeCallback,
                favoriteCallback,
                shareCallback,
                manager.storyId
        );
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    IChangeStatusReaderCallback favoriteCallback = new IChangeStatusReaderCallback() {
        @Override
        public void onProcess() {
            post(new Runnable() {
                @Override
                public void run() {
                    favorite.setEnabled(false);
                    favorite.setClickable(false);
                }
            });
        }

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
    };

    IChangeStatusReaderCallback shareCallback = new IChangeStatusReaderCallback() {

        @Override
        public void onProcess() {
            post(new Runnable() {
                @Override
                public void run() {
                    share.setEnabled(false);
                    share.setClickable(false);
                }
            });
            manager.getParentManager().pauseSlide(false);
        }

        @Override
        public void onSuccess(int val) {
            post(new Runnable() {
                @Override
                public void run() {
                    share.setEnabled(true);
                    share.setClickable(true);
                }
            });
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
    };

    public void soundClick() {
        manager.soundClick();
    }

    public void shareClick() {
        manager.shareClick(shareCallback);
    }
}
