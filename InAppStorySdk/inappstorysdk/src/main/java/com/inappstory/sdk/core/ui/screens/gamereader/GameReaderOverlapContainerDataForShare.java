package com.inappstory.sdk.core.ui.screens.gamereader;


import com.inappstory.sdk.core.ui.screens.holder.IOverlapContainerData;
import com.inappstory.sdk.core.ui.screens.holder.OverlapContainerHolderType;
import com.inappstory.sdk.share.IASShareData;

public class GameReaderOverlapContainerDataForShare implements IOverlapContainerData {
    public GameReaderOverlapContainerDataForShare() {}

    int storyId;
    int slideIndex;
    IASShareData shareData;

    public GameReaderOverlapContainerDataForShare storyId(int storyId) {
        this.storyId = storyId;
        return this;
    }

    public GameReaderOverlapContainerDataForShare slideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
        return this;
    }

    public GameReaderOverlapContainerDataForShare shareData(IASShareData shareData) {
        this.shareData = shareData;
        return this;
    }

    @Override
    public OverlapContainerHolderType getHolderType() {
        return OverlapContainerHolderType.GAME;
    }
}
