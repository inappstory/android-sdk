package com.inappstory.sdk.core.ui.widgets.customicons;

public interface IIASDefaultIconCreator {
    CustomIconWithStates generateDefaultIcon(final int iconId);

    CustomIconWithoutStates generateDefaultStatelessIcon(final int iconId);
}
