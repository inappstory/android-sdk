package com.inappstory.sdk;


import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithStates;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithoutStates;

public interface ICustomAppearanceIcons {
    CustomIconWithStates favoriteIcon();
    CustomIconWithStates likeIcon();
    CustomIconWithStates dislikeIcon();
    CustomIconWithStates shareIcon();
    CustomIconWithStates soundIcon();
    CustomIconWithoutStates closeIcon();
    CustomIconWithoutStates refreshIcon();
}
