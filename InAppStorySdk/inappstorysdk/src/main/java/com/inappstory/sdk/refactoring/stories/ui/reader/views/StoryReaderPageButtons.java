package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.ICustomAppearanceIcons;
import com.inappstory.sdk.ICustomIcon;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconState;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderButtonState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderButtonsState;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderPageViewModel;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoryReaderPageButtons extends LinearLayout implements Observer<StoryReaderButtonsState> {


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


    public void viewModel(StoryReaderPageViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.addButtonsStateSubscriber(this);
    }

    StoryReaderPageViewModel viewModel;

    public StoryReaderPageButtons(Context context) {
        super(context);
        init(context);
    }

    public StoryReaderPageButtons(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StoryReaderPageButtons(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
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
        like.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        dislike.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        favorite.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        sound.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        share.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        likeLayout.addView(like);
        dislikeLayout.addView(dislike);
        shareLayout.addView(share);
        soundLayout.addView(sound);
        favoriteLayout.addView(favorite);
        setListeners();
    }

    private void setListeners() {
        if (likeLayout != null) {
            likeLayout.setClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewModel != null) viewModel.likeClick();
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
                    if (viewModel != null) viewModel.dislikeClick();
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
                    if (viewModel != null) viewModel.favoriteClick();
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
                    if (viewModel != null) viewModel.shareClick();
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
                    if (viewModel != null) viewModel.soundClick();
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
        }
    }

    StoryReaderButtonState likeState = new StoryReaderButtonState();
    StoryReaderButtonState dislikeState = new StoryReaderButtonState();
    StoryReaderButtonState favoriteState = new StoryReaderButtonState();
    StoryReaderButtonState shareState = new StoryReaderButtonState();
    StoryReaderButtonState soundState = new StoryReaderButtonState();

    private void updateButtonState(
            TouchFrameLayout buttonLayout,
            View buttonView,
            ICustomIcon buttonInterface,
            StoryReaderButtonState state
    ) {
        if (!state.visible()) {
            buttonLayout.setVisibility(GONE);
        } else {
            buttonLayout.setVisibility(VISIBLE);
            buttonLayout.setClickable(state.enabled());
            buttonInterface.updateState(
                    buttonView,
                    new CustomIconState(state.active(), state.enabled())
            );
        }
    }

    @Override
    public void onUpdate(StoryReaderButtonsState newValue) {
        if (newValue.likeState() != null && !newValue.likeState().equals(likeState)) {
            likeState = newValue.likeState();
            updateButtonState(likeLayout, like, likeInterface, likeState);
        }
        if (newValue.dislikeState() != null && !newValue.dislikeState().equals(dislikeState)) {
            dislikeState = newValue.dislikeState();
            updateButtonState(dislikeLayout, dislike, dislikeInterface, dislikeState);
        }
        if (newValue.favoriteState() != null && !newValue.favoriteState().equals(favoriteState)) {
            favoriteState = newValue.favoriteState();
            updateButtonState(favoriteLayout, favorite, favoriteInterface, favoriteState);
        }
        if (newValue.shareState() != null && !newValue.shareState().equals(shareState)) {
            shareState = newValue.shareState();
            updateButtonState(shareLayout, share, shareInterface, shareState);
        }
        if (newValue.soundState() != null && !newValue.soundState().equals(soundState)) {
            soundState = newValue.soundState();
            updateButtonState(soundLayout, sound, soundInterface, soundState);
        }
    }
}
