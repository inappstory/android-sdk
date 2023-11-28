package com.inappstory.sdk.stories.ui.reader;




public class StoriesMainPhoneFragment extends StoriesMainFragment {
    @Override
    void openAnimationProgress(float progress) {

    }

    @Override
    void openAnimationStart() {
        backTintView.setBackgroundColor(appearanceSettings.csReaderBackgroundColor());
    }


    @Override
    void closeAnimationProgress(float progress) {

    }

    @Override
    void reInitUI() {
        backTintView.setBackgroundColor(appearanceSettings.csReaderBackgroundColor());
    }


    @Override
    void onDrag(float rawOffset) {
        backTintView.setAlpha(Math.min(1f, Math.max(0f, 1f - rawOffset)));
    }

    @Override
    void outsideClick() {

    }
}
