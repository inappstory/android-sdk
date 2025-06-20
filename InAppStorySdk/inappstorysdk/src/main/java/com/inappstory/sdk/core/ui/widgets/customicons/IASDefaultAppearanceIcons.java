package com.inappstory.sdk.core.ui.widgets.customicons;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.ICustomAppearanceIcons;
import com.inappstory.sdk.ICustomIcon;
import com.inappstory.sdk.ICustomIconWithoutStates;

public class IASDefaultAppearanceIcons implements ICustomAppearanceIcons {
    private final AppearanceManager appearanceManager;
    ICustomIcon

    public IASDefaultAppearanceIcons(AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
    }

    @Override
    public ICustomIcon favoriteIcon() {
        return null;
    }

    @Override
    public ICustomIcon likeIcon() {
        return null;
    }

    @Override
    public ICustomIcon dislikeIcon() {
        return null;
    }

    @Override
    public ICustomIcon shareIcon() {
        return null;
    }

    @Override
    public ICustomIcon soundIcon() {
        return null;
    }

    @Override
    public ICustomIconWithoutStates closeIcon() {
        return null;
    }

    @Override
    public ICustomIconWithoutStates refreshIcon() {
        return null;
    }
}
