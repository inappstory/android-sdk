package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.content.Context;
import android.os.Bundle;

public interface IOpenReader {
    void onOpen(
            Context context,
            Bundle bundle
    );

    void onHideStatusBar(Context context);

    void onRestoreStatusBar(Context context);

    void onShowInFullscreen(Context context);

    void onRestoreScreen(Context context);
}