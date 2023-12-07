package com.inappstory.sdk.stories.uidomain.reader.views.bottompanel;

public final class BottomPanelFavoriteState {

    public BottomPanelFavoriteState favorite(boolean favorite) {
        this.favorite = favorite;
        return this;
    }

    public BottomPanelFavoriteState favoriteEnabled(boolean favoriteEnabled) {
        this.favoriteEnabled = favoriteEnabled;
        return this;
    }

    public boolean favorite() {
        return favorite;
    }

    public boolean favoriteEnabled() {
        return favoriteEnabled;
    }


    public BottomPanelFavoriteState(
            boolean favorite,
            boolean favoriteEnabled
    ) {
        this.favorite = favorite;
        this.favoriteEnabled = favoriteEnabled;
    }

    private boolean favorite;
    private boolean favoriteEnabled;

    public BottomPanelFavoriteState() {
        this.favoriteEnabled = true;
    }

    public BottomPanelFavoriteState copy() {
        return new BottomPanelFavoriteState(
                this.favorite,
                this.favoriteEnabled
        );
    }
}
