package com.inappstory.sdk.stories.uidomain.reader.views.bottompanel;

public final class BottomPanelLikeState {
    public BottomPanelLikeState like(int like) {
        this.like = like;
        return this;
    }

    public BottomPanelLikeState likeEnabled(boolean likeEnabled) {
        this.likeEnabled = likeEnabled;
        return this;
    }

    public int like() {
        return like;
    }

    public boolean likeEnabled() {
        return likeEnabled;
    }

    public BottomPanelLikeState(
            int like,
            boolean likeEnabled
    ) {
        this.like = like;
        this.likeEnabled = likeEnabled;
    }

    private int like;
    private boolean likeEnabled;

    public BottomPanelLikeState() {
        this.likeEnabled = true;
    }

    public BottomPanelLikeState copy() {
        return new BottomPanelLikeState(
                this.like,
                this.likeEnabled
        );
    }
}
