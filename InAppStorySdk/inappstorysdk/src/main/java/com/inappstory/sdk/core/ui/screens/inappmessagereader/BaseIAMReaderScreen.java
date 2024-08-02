package com.inappstory.sdk.core.ui.screens.inappmessagereader;


import androidx.fragment.app.FragmentManager;


public interface BaseIAMReaderScreen {
    void closeInAppMessageReader(int action);
    void forceFinish();
    void timerIsLocked();
    void timerIsUnlocked();
    void pauseReader();
    void resumeReader();
    void disableDrag(boolean disable);
    FragmentManager getInAppMessageReaderFragmentManager();
}
