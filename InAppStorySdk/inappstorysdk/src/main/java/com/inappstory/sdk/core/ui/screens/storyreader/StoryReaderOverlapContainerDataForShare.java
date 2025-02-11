package com.inappstory.sdk.core.ui.screens.storyreader;


import com.inappstory.sdk.core.ui.screens.holder.IOverlapContainerData;
import com.inappstory.sdk.core.ui.screens.holder.OverlapContainerHolderType;
import com.inappstory.sdk.share.IASShareData;

public class StoryReaderOverlapContainerDataForShare implements IOverlapContainerData {
    public StoryReaderOverlapContainerDataForShare() {}


    public StoryReaderOverlapContainerDataForShare slidePayload(String slidePayload) {
        this.slidePayload = slidePayload;
        return this;
    }

    public StoryReaderOverlapContainerDataForShare storyId(int storyId) {
        this.storyId = storyId;
        return this;
    }

    public StoryReaderOverlapContainerDataForShare slideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
        return this;
    }

    public StoryReaderOverlapContainerDataForShare shareData(IASShareData shareData) {
        this.shareData = shareData;
        return this;
    }

    String slidePayload;
    int storyId;
    int slideIndex;
    IASShareData shareData;

    @Override
    public OverlapContainerHolderType getHolderType() {
        return OverlapContainerHolderType.STORY;
    }
}
