package com.inappstory.sdk.stories.ui.widgets.readerscreen.generated;

import android.content.Context;
import android.graphics.Point;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.utils.Sizes;

public class GeneratedViewSizes {
    public static Point getSizes() {
        Point pt = Sizes.getScreenSize();
        pt.y -= Sizes.dpToPxExt(60);
        pt.y -= fixedDrop;
        return pt;
    }

    public static int fixedDrop = 0;

    public static int getEMInPx() {
        return getSizes().x / 20;
    }

    public static int getEMInDp() {
        return Sizes.pxToDpExt(getEMInPx());
    }
}
