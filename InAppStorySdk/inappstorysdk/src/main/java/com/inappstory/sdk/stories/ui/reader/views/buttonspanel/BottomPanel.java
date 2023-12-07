package com.inappstory.sdk.stories.ui.reader.views.buttonspanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.interfaces.IChangeStatusReaderCallback;
import com.inappstory.sdk.databinding.IasReaderButtonsPanelBinding;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.ui.oldreader.StoriesReaderSettings;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.BottomPanelLikeState;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.IBottomPanelViewModel;

public class BottomPanel extends LinearLayout {


    private IBottomPanelViewModel viewModel;

    public BottomPanel(Context context) {
        super(context);
        init();
    }

    public BottomPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public BottomPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public void refreshSoundStatus() {
        sound.setActivated(IASCore.getInstance().isSoundOn());
    }

    IasReaderButtonsPanelBinding binding;

    public void setViewModel(IBottomPanelViewModel viewModel) {
        this.viewModel = viewModel;
        if (isAttachedToWindow()) {
            observeStates();
        }
    }

    private void observeStates() {
        viewModel.likeStateLD().observeForever(new Observer<BottomPanelLikeState>() {
            @Override
            public void onChanged(BottomPanelLikeState bottomPanelLikeState) {

            }
        });
    }

    private void removeObservers() {

    }


    public void init() {
        binding = IasReaderButtonsPanelBinding.inflate(LayoutInflater.from(getContext()));
        binding.iasLikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                likeClick();
            }
        });
        binding.iasDislikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikeClick();
            }
        });
        binding.iasFavoriteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteClick();
            }
        });
        binding.iasShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareClick();
            }
        });
        binding.iasSoundButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick();
            }
        });
        binding.iasSoundButton.setActivated(IASCore.getInstance().isSoundOn());
    }

    public void setIcons(StoriesReaderAppearanceSettings appearanceSettings) {
        binding.iasLikeButton.setImageDrawable(
                getResources().getDrawable(appearanceSettings.csLikeIcon())
        );
        binding.iasDislikeButton.setImageDrawable(
                getResources().getDrawable(appearanceSettings.csDislikeIcon())
        );
        binding.iasFavoriteButton.setImageDrawable(
                getResources().getDrawable(appearanceSettings.csFavoriteIcon())
        );
        binding.iasShareButton.setImageDrawable(
                getResources().getDrawable(appearanceSettings.csShareIcon())
        );
        binding.iasSoundButton.setImageDrawable(
                getResources().getDrawable(appearanceSettings.csSoundIcon())
        );
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
        if (viewModel != null) {
            observeStates();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (viewModel != null) {
            removeObservers();
        }
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
