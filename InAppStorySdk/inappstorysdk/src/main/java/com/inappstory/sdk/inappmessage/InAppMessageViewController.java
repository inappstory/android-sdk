package com.inappstory.sdk.inappmessage;

import com.inappstory.sdk.core.ui.screens.inappmessagereader.BaseIAMScreen;
import com.inappstory.sdk.inappmessage.domain.reader.IAMViewController;
import com.inappstory.sdk.inappmessage.ui.reader.InAppMessageMainView;

public final class InAppMessageViewController implements IAMViewController {
    BaseIAMScreen inAppMessageMainView = null;

    public void subscribeView(BaseIAMScreen inAppMessageMainView) {
        this.inAppMessageMainView = inAppMessageMainView;
    }

    public void unsubscribeView(BaseIAMScreen inAppMessageMainView) {
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
