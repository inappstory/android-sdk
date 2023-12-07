package com.inappstory.sdk.stories.uidomain.reader.views.bottompanel;

public final class BottomPanelVisibilityState {
    public BottomPanelVisibilityState hasFavorite(boolean hasFavorite) {
        this.hasFavorite = hasFavorite;
        return this;
    }

    public BottomPanelVisibilityState hasLike(boolean hasLike) {
        this.hasLike = hasLike;
        this.hasDislike = hasLike;
        return this;
    }

    public BottomPanelVisibilityState hasDislike(boolean hasDislike) {
        this.hasLike = hasDislike;
        this.hasDislike = hasDislike;
        return this;
    }

    public BottomPanelVisibilityState hasShare(boolean hasShare) {
        this.hasShare = hasShare;
        return this;
    }

    public BottomPanelVisibilityState hasSound(boolean hasSound) {
        this.hasSound = hasSound;
        return this;
    }

    public boolean hasFavorite() {
        return hasFavorite;
    }

    public boolean hasLike() {
        return hasLike;
    }

    public boolean hasDislike() {
        return hasDislike;
    }

    public boolean hasShare() {
        return hasShare;
    }

    public boolean hasSound() {
        return hasSound;
    }

    public BottomPanelVisibilityState(
            boolean hasFavorite,
            boolean hasLike,
            boolean hasDislike,
            boolean hasShare,
            boolean hasSound
    ) {
        this.hasFavorite = hasFavorite;
        this.hasLike = hasLike;
        this.hasDislike = hasDislike;
        this.hasShare = hasShare;
        this.hasSound = hasSound;
    }

    private boolean hasFavorite;
    private boolean hasLike;
    private boolean hasDislike;
    private boolean hasShare;
    private boolean hasSound;

    public boolean isVisible() {
        return hasFavorite || hasLike || hasDislike || hasShare || hasSound;
    }


    public BottomPanelVisibilityState() {}

    public BottomPanelVisibilityState copy() {
        return new BottomPanelVisibilityState(
                this.hasFavorite,
                this.hasLike,
                this.hasDislike,
                this.hasShare,
                this.hasSound
        );
    }
}
