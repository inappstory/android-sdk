package com.inappstory.sdk.core.ui.widgets.customicons;

import android.content.Context;
import android.util.SizeF;
import android.view.View;

import com.inappstory.sdk.ICustomIconState;

public class IASDefaultIconCreator implements IIASDefaultIconCreator {
    public CustomIconWithStates generateDefaultIcon(final int iconId) {
        return new CustomIconWithStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(iconId);
            }

            @Override
            public void updateState(View iconView, ICustomIconState state) {
                if (iconView instanceof IASDefaultIcon) {
                    ((IASDefaultIcon) iconView).updateState(state);
                }
            }
        };
    }

    public CustomIconWithoutStates generateDefaultStatelessIcon(final int iconId) {
        return new CustomIconWithoutStates() {
            @Override
            public View createIconView(Context context, SizeF maxSizeInPx) {
                return new IASDefaultIcon(context).setIconId(iconId);
            }
        };
    }
}
