package com.inappstory.sdk.stories.ui.reader;

public class StoriesMainTabletFragment extends StoriesMainFragment {
    @Override
    void openAnimationProgress(float progress) {
        backTintView.setAlpha(0.3f * progress);
    }

    @Override
    void openAnimationStart() {
        backTintView.setAlpha(0.0f);
        backTintView.setBackgroundColor(appearanceSettings.csReaderBackgroundColor());
    }


    @Override
    void closeAnimationProgress(float progress) {
        backTintView.setAlpha(0.3f * (1f - progress));
    }

    @Override
    void reInitUI() {
        backTintView.setAlpha(0.3f);
        backTintView.setBackgroundColor(appearanceSettings.csReaderBackgroundColor());
    }


    @Override
    void onDrag(float rawOffset) {
        backTintView.setAlpha(Math.min(0.3f, Math.max(0f, 0.3f * (1f - rawOffset))));
    }

    @Override
    void outsideClick() {
        closeWithAction(-1);
    }
}
