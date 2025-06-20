package com.inappstory.sdk.core.ui.widgets.customicons;

import android.content.Context;
import android.util.SizeF;
import android.view.View;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.CustomIconState;
import com.inappstory.sdk.ICustomAppearanceIcons;
import com.inappstory.sdk.ICustomIcon;
import com.inappstory.sdk.ICustomIconWithoutStates;

public class IASDefaultAppearanceIcons implements ICustomAppearanceIcons {
    private final AppearanceManager appearanceManager;

    public IASDefaultAppearanceIcons(@NonNull AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
    }

    @Override
    public ICustomIcon favoriteIcon() {
        return new ICustomIcon() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csFavoriteIcon());
            }

            @Override
            public void updateState(View iconView, boolean active, boolean enabled) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(active, enabled);
                }
            }
        };
    }

    @Override
    public ICustomIcon likeIcon() {
        return new ICustomIcon() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csLikeIcon());
            }

            @Override
            public void updateState(View iconView, boolean active, boolean enabled) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(active, enabled);
                }
            }
        };
    }

    @Override
    public ICustomIcon dislikeIcon() {
        return new ICustomIcon() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csDislikeIcon());
            }

            @Override
            public void updateState(View iconView, boolean active, boolean enabled) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(active, enabled);
                }
            }
        };
    }

    @Override
    public ICustomIcon shareIcon() {
        return new ICustomIcon() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csShareIcon());
            }

            @Override
            public void updateState(View iconView, boolean active, boolean enabled) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(active, enabled);
                }
            }
        };
    }

    @Override
    public ICustomIcon soundIcon() {
        return new ICustomIcon() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csSoundIcon());
            }

            @Override
            public void updateState(View iconView, boolean active, boolean enabled) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(active, enabled);
                }
            }
        };
    }

    @Override
    public ICustomIconWithoutStates closeIcon() {
        return new ICustomIconWithoutStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csCloseIcon());
            }
        };
    }

    @Override
    public ICustomIconWithoutStates refreshIcon() {
        return new ICustomIconWithoutStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csRefreshIcon());
            }
        };
    }
}
