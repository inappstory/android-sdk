package com.inappstory.sdk.core.utils;

import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;

public class CallbackTypesConverter {
    public SourceType getSourceFromInt(int intSourceType) {
        SourceType sourceType = SourceType.LIST;
        switch (intSourceType) {
            case CloseStory.SINGLE:
                sourceType = SourceType.SINGLE;
                break;
            case CloseStory.ONBOARDING:
                sourceType = SourceType.ONBOARDING;
                break;
            case CloseStory.FAVORITE:
                sourceType = SourceType.FAVORITE;
                break;
        }
        return sourceType;
    }

    public ShowStoryAction getShowStoryActionTypeFromInt(int intActionType) {
        ShowStoryAction type = ShowStoryAction.OPEN;
        switch (intActionType) {
            case ShowStory.ACTION_AUTO:
                type = ShowStoryAction.AUTO;
                break;
            case ShowStory.ACTION_TAP:
                type = ShowStoryAction.TAP;
                break;
            case ShowStory.ACTION_CUSTOM:
                type = ShowStoryAction.CUSTOM;
                break;
            case ShowStory.ACTION_SWIPE:
                type = ShowStoryAction.SWIPE;
                break;
        }
        return type;
    }

    public CloseReader getCloseTypeFromInt(int intCloseType) {
        CloseReader closeType = CloseReader.CLICK;
        switch (intCloseType) {
            case CloseStory.AUTO:
                closeType = CloseReader.AUTO;
                break;
            case CloseStory.SWIPE:
                closeType = CloseReader.SWIPE;
                break;
            case CloseStory.CUSTOM:
            case -1:
                closeType = CloseReader.CUSTOM;
                break;
        }
        return closeType;
    }
}
