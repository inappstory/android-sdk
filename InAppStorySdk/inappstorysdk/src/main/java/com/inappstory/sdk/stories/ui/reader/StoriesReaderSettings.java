package com.inappstory.sdk.stories.ui.reader;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_OVERSCROLL;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_DISLIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_FAVORITE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_FAVORITE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_LIKE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_SHARE;
import static com.inappstory.sdk.AppearanceManager.CS_LIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_READER_BACKGROUND_COLOR;
import static com.inappstory.sdk.AppearanceManager.CS_READER_RADIUS;
import static com.inappstory.sdk.AppearanceManager.CS_REFRESH_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SHARE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SOUND_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT_ENABLE;

import android.graphics.Color;
import android.os.Bundle;

import com.inappstory.sdk.R;

public class StoriesReaderSettings {
    public boolean closeOnSwipe;
    public boolean closeOnOverscroll;
    public int closePosition;
    public int readerAnimation;
    public boolean hasLike;
    public boolean hasFavorite;
    public boolean hasShare;
    public int favoriteIcon;
    public int likeIcon;
    public int dislikeIcon;
    public int shareIcon;
    public int closeIcon;
    public int refreshIcon;
    public int soundIcon;
    public boolean timerGradientEnable;
    public int radius;
    public int panelColor;
    public int backgroundColor;

    public StoriesReaderSettings() {}

    public StoriesReaderSettings(Bundle bundle) {
        this.closeOnSwipe = bundle.getBoolean(CS_CLOSE_ON_SWIPE, true);
        this.closeOnOverscroll = bundle.getBoolean(CS_CLOSE_ON_OVERSCROLL, true);
        this.closePosition = bundle.getInt(CS_CLOSE_POSITION, 1);
        this.hasLike = bundle.getBoolean(CS_HAS_LIKE, false);
        this.hasFavorite = bundle.getBoolean(CS_HAS_FAVORITE, false);
        this.hasShare = bundle.getBoolean(CS_HAS_SHARE, false);
        this.favoriteIcon = bundle.getInt(CS_FAVORITE_ICON, R.drawable.ic_stories_status_favorite);
        this.likeIcon = bundle.getInt(CS_LIKE_ICON, R.drawable.ic_stories_status_like);
        this.dislikeIcon = bundle.getInt(CS_DISLIKE_ICON, R.drawable.ic_stories_status_dislike);
        this.shareIcon = bundle.getInt(CS_SHARE_ICON, R.drawable.ic_share_status);
        this.closeIcon = bundle.getInt(CS_CLOSE_ICON, R.drawable.ic_stories_close);
        this.refreshIcon = bundle.getInt(CS_REFRESH_ICON, R.drawable.ic_refresh);
        this.soundIcon = bundle.getInt(CS_SOUND_ICON, R.drawable.ic_stories_status_sound);
        this.timerGradientEnable = bundle.getBoolean(CS_TIMER_GRADIENT_ENABLE, true);
        this.radius = bundle.getInt(CS_READER_RADIUS, 0);
        this.backgroundColor = bundle.getInt(CS_READER_BACKGROUND_COLOR, Color.BLACK);
    }
}
