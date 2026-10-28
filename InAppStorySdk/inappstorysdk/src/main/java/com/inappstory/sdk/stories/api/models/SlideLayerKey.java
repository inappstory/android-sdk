package com.inappstory.sdk.stories.api.models;

import java.util.Objects;

public class SlideLayerKey {
    public final int cardId;
    public final int slideIndex;
    public final int layerIndex;

    public SlideLayerKey(int cardId, int slideIndex, int layerIndex) {
        this.cardId = cardId;
        this.slideIndex = slideIndex;
        this.layerIndex = layerIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SlideLayerKey)) return false;
        SlideLayerKey that = (SlideLayerKey) o;
        return cardId == that.cardId &&
                slideIndex == that.slideIndex &&
                layerIndex == that.layerIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, slideIndex, layerIndex);
    }
}
