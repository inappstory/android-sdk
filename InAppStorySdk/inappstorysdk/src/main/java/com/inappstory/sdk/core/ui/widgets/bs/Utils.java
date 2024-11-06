package com.inappstory.sdk.core.ui.widgets.bs;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

public final class Utils {

    /**
     * Retrieves the size (height) of the system status bar.
     *
     * @param context a valid context
     * @return the retrieved size (height) of the system status bar
     */
    public static int getStatusBarSize(@NonNull Context context) {
        Preconditions.nonNull(context);

        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        );

        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * Retrieves the size (height) of the system navigation bar.
     *
     * @param context a valid context
     * @return the retrieved size (height) of the system navigation bar
     */
    public static int getNavigationBarSize(@NonNull Context context) {
        Preconditions.nonNull(context);

        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(
            "navigation_bar_height",
            "dimen",
            "android"
        );

        return resources.getDimensionPixelSize(resourceId);
    }

}