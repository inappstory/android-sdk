package com.inappstory.sdk.stories.ui.reader;

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


    public StoriesReaderSettings(boolean closeOnSwipe, boolean closeOnOverscroll,
                                 int closePosition,
                                 boolean hasLike, boolean hasFavorite,
                                 boolean hasShare, int favoriteIcon,
                                 int likeIcon, int dislikeIcon,
                                 int shareIcon, int closeIcon,
                                 int refreshIcon, int soundIcon,
                                 boolean timerGradientEnable) {
        this.closeOnSwipe = closeOnSwipe;
        this.closeOnOverscroll = closeOnOverscroll;
        this.closePosition = closePosition;
        //  this.readerAnimation = readerAnimation;
        this.hasLike = hasLike;
        this.hasFavorite = hasFavorite;
        this.hasShare = hasShare;
        this.favoriteIcon = favoriteIcon;
        this.likeIcon = likeIcon;
        this.dislikeIcon = dislikeIcon;
        this.shareIcon = shareIcon;
        this.closeIcon = closeIcon;
        this.refreshIcon = refreshIcon;
        this.soundIcon = soundIcon;
        this.timerGradientEnable = timerGradientEnable;
    }

    public StoriesReaderSettings() {
    }
}
