package com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel;

public class ButtonPanelFavoriteClickCallback implements ButtonPanelClickCallback {
    private ButtonsPanel panel;
    private final Object lock = new Object();

    @Override
    public void subscribeView(ButtonsPanel panel) {
        synchronized (lock) {
            this.panel = panel;
        }
    }

    @Override
    public void unsubscribeView(ButtonsPanel panel) {
        synchronized (lock) {
            if (this.panel == panel)
                this.panel = null;
        }
    }

    @Override
    public void onSuccess(final int val) {
        final ButtonsPanel panel;
        synchronized (lock) {
            panel = this.panel;
        }
        if (panel == null) return;
        panel.post(new Runnable() {
            @Override
            public void run() {
                panel.favorite.setEnabled(true);
                panel.favorite.setClickable(true);
                panel.favorite.setActivated(val == 1);
            }
        });
    }

    @Override
    public void onError() {
        final ButtonsPanel panel;
        synchronized (lock) {
            panel = this.panel;
        }
        if (panel == null) return;
        panel.post(new Runnable() {
            @Override
            public void run() {
                panel.favorite.setEnabled(true);
                panel.favorite.setClickable(true);
            }
        });
    }
}
