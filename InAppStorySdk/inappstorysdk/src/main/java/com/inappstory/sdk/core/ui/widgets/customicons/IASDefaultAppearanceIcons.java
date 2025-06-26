package com.inappstory.sdk.core.ui.widgets.customicons;

import android.content.Context;
import android.util.SizeF;
import android.view.View;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.ICustomAppearanceIcons;
import com.inappstory.sdk.ICustomIconState;

public class IASDefaultAppearanceIcons implements ICustomAppearanceIcons {
    private final AppearanceManager appearanceManager;

    public IASDefaultAppearanceIcons(@NonNull AppearanceManager appearanceManager) {
        this.appearanceManager = appearanceManager;
    }

    @Override
    public CustomIconWithStates favoriteIcon() {
        return new CustomIconWithStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csFavoriteIcon());
            }

            @Override
            public void updateState(View iconView, ICustomIconState iconState) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(iconState);
                }
            }
        };
    }

    @Override
    public CustomIconWithStates likeIcon() {
        return new CustomIconWithStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csLikeIcon());
            }

            @Override
            public void updateState(View iconView, ICustomIconState iconState) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(iconState);
                }
            }
        };
    }

    @Override
    public CustomIconWithStates dislikeIcon() {
        return new CustomIconWithStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csDislikeIcon());
            }

            @Override
            public void updateState(View iconView, ICustomIconState iconState) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(iconState);
                }
            }
        };
    }

    @Override
    public CustomIconWithStates shareIcon() {
        return new CustomIconWithStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csShareIcon());
            }

            @Override
            public void updateState(View iconView, ICustomIconState iconState) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(iconState);
                }
            }
        };
    }

    @Override
    public CustomIconWithStates soundIcon() {
        return new CustomIconWithStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csSoundIcon());
            }

            @Override
            public void updateState(View iconView, ICustomIconState iconState) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(iconState);
                }
            }
        };
    }

    @Override
    public CustomIconWithoutStates closeIcon() {
        return new CustomIconWithoutStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csCloseIcon());
            }
        };
    }

    @Override
    public CustomIconWithoutStates refreshIcon() {
        return new CustomIconWithoutStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(appearanceManager.csRefreshIcon());
            }
        };
    }
}
