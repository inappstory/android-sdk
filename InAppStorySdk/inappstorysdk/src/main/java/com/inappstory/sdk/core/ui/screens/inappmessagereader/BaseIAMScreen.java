package com.inappstory.sdk.core.ui.screens.inappmessagereader;

import com.inappstory.sdk.core.ui.screens.BaseScreen;


public interface BaseIAMScreen extends BaseScreen {
    void closeWithAction(int action);
    void timerIsLocked();
    void timerIsUnlocked();
    void disableDrag(boolean disable);
}
