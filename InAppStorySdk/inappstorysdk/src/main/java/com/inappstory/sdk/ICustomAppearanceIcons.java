package com.inappstory.sdk;

import android.content.Context;

public interface ICustomAppearanceIcons {
    ICustomIcon favoriteIcon(Context context);
    ICustomIcon likeIcon();
    ICustomIcon dislikeIcon();
    ICustomIcon shareIcon();
    ICustomIcon soundIcon();
    ICustomIconWithoutStates closeIcon();
    ICustomIconWithoutStates refreshIcon();
}
