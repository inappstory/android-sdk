package com.inappstory.sdk.stories.ui.reader.views.bottompanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.databinding.IasReaderButtonsPanelBinding;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.BottomPanelFavoriteState;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.BottomPanelLikeState;
import com.inappstory.sdk.stories.uidomain.reader.views.bottompanel.BottomPanelVisibilityState;
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


    IasReaderButtonsPanelBinding binding;

    public void setViewModel(IBottomPanelViewModel viewModel) {
        this.viewModel = viewModel;
        if (isAttachedToWindow()) {
            setVisibility();
            observeStates();
        }
    }

    private void setEnabledAndClickable(View view, boolean status) {
        view.setEnabled(status);
        view.setClickable(status);
    }

    Observer<BottomPanelLikeState> likeStateObserver = new Observer<BottomPanelLikeState>() {
        @Override
        public void onChanged(@NonNull BottomPanelLikeState likeState) {
            View likeButton = binding.iasLikeButton;
            View dislikeButton = binding.iasDislikeButton;
            setEnabledAndClickable(likeButton, likeState.likeEnabled());
            setEnabledAndClickable(dislikeButton, likeState.likeEnabled());
            likeButton.setActivated(likeState.like() == 1);
            dislikeButton.setActivated(likeState.like() == -1);
        }
    };

    Observer<BottomPanelFavoriteState> favoriteStateObserver = new Observer<BottomPanelFavoriteState>() {
        @Override
        public void onChanged(@NonNull BottomPanelFavoriteState favoriteState) {
            View favoriteButton = binding.iasFavoriteButton;
            setEnabledAndClickable(favoriteButton, favoriteState.favoriteEnabled());
            favoriteButton.setActivated(favoriteState.favorite());
        }
    };

    Observer<Boolean> shareStateObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(@NonNull Boolean shareEnabled) {
            setEnabledAndClickable(binding.iasShareButton, shareEnabled);
        }
    };

    Observer<Boolean> soundStateObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(@NonNull Boolean soundOn) {
            binding.iasSoundButton.setActivated(soundOn);
        }
    };

    public void setVisibility() {
        BottomPanelVisibilityState visibilityState = viewModel.visibilityState();
        binding.iasFavoriteButton.setVisibility(visibilityState.hasFavorite() ? VISIBLE : GONE);
        binding.iasLikeButton.setVisibility(visibilityState.hasLike() ? VISIBLE : GONE);
        binding.iasDislikeButton.setVisibility(visibilityState.hasDislike() ? VISIBLE : GONE);
        binding.iasShareButton.setVisibility(visibilityState.hasShare() ? VISIBLE : GONE);
        binding.iasSoundButton.setVisibility(visibilityState.hasSound() ? VISIBLE : GONE);
        binding.getRoot().setVisibility(visibilityState.isVisible() ? VISIBLE : GONE);
    }

    private void observeStates() {
        viewModel.likeStateLD().observeForever(likeStateObserver);
        viewModel.favoriteStateLD().observeForever(favoriteStateObserver);
        viewModel.shareEnabledStateLD().observeForever(shareStateObserver);
        viewModel.soundOnStateLD().observeForever(soundStateObserver);
    }

    private void removeObservers() {
        viewModel.likeStateLD().removeObserver(likeStateObserver);
        viewModel.favoriteStateLD().removeObserver(favoriteStateObserver);
        viewModel.shareEnabledStateLD().removeObserver(shareStateObserver);
        viewModel.soundOnStateLD().removeObserver(soundStateObserver);
    }


    public void init() {
        binding = IasReaderButtonsPanelBinding.inflate(LayoutInflater.from(getContext()),
                this,
                true
        );
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


    public void likeClick() {
        viewModel.likeClick();
    }

    public void dislikeClick() {
        viewModel.dislikeClick();
    }

    public void favoriteClick() {
        viewModel.favoriteClick();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (viewModel != null) {
            setVisibility();
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

    public void soundClick() {
        viewModel.soundClick();
    }

    public void shareClick() {
        viewModel.shareClick();
    }
}
