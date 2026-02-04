package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.inappmessage.domain.reader.IAMViewController;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainView;

public final class InAppMessageViewController implements IAMViewController {
    InAppMessageMainView inAppMessageMainView = null;

    public void subscribeView(InAppMessageMainView inAppMessageMainView) {
        this.inAppMessageMainView = inAppMessageMainView;
    }

    public void unsubscribeView(InAppMessageMainView inAppMessageMainView) {
        if (this.inAppMessageMainView == inAppMessageMainView) {
            this.inAppMessageMainView = null;
        }
    }

    @Override
    public void pauseView() {
        if (inAppMessageMainView != null) {
            inAppMessageMainView.pauseScreen();
        }
    }

    @Override
    public void resumeView() {
        if (inAppMessageMainView != null) {
            inAppMessageMainView.resumeScreen();
        }
    }

    @Override
    public void closeView() {
        if (inAppMessageMainView != null) {
            inAppMessageMainView.forceFinish();
        }
    }
}
