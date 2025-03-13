package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

public interface ButtonPanelClickCallback extends ButtonClickCallback {
    void subscribeView(ButtonsPanel panel);
    void unsubscribeView(ButtonsPanel panel);
}
