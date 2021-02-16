package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.inappstory.sdk.R;

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

    void setVisibility(boolean hasLike, boolean hasFavorite, boolean hasShare, boolean hasSound) {
        like.setVisibility(hasLike ? VISIBLE : GONE);
        dislike.setVisibility(hasLike ? VISIBLE : GONE);
        favorite.setVisibility(hasFavorite ? VISIBLE : GONE);
        share.setVisibility(hasShare ? VISIBLE : GONE);
        sound.setVisibility(hasSound ? VISIBLE : GONE);
    }

    void init() {
        inflate(getContext(), R.layout.cs_buttons_panel, this);
        like = findViewById(R.id.likeButton);
        dislike = findViewById(R.id.dislikeButton);
        favorite = findViewById(R.id.favoriteButton);
        sound = findViewById(R.id.soundButton);
        share = findViewById(R.id.shareButton);
        like.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                likeClick();
            }
        });
        dislike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikeClick();
            }
        });
        favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteClick();
            }
        });
        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareClick();
            }
        });
        sound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                soundClick();
            }
        });
    }

    void likeClick() {
    }

    void dislikeClick() {
    }

    void favoriteClick() {
    }

    void soundClick() {
    }

    void shareClick() {
    }
}
