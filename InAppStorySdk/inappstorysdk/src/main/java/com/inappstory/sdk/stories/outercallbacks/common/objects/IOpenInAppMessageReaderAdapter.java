package com.inappstory.sdk.stories.outercallbacks.common.objects;

import android.content.Context;
import android.os.Bundle;

public abstract class IOpenInAppMessageReaderAdapter implements IOpenInAppMessageReader {
    public abstract void onOpen(
            Context context,
            Bundle bundle
    );

    public void onHideStatusBar(Context context) {
    }

    public void onRestoreStatusBar(Context context) {
    }

    public void onShowInFullscreen(Context context) {
    }

    public void onRestoreScreen(Context context) {
    }
}
