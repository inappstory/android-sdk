package com.inappstory.sdk.refactoring.stories.ui.reader.states;

import com.inappstory.sdk.refactoring.stories.ui.list.states.StoryListItemCoordinates;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

import java.util.ArrayList;
import java.util.List;

public class StoryReaderState {

    private boolean horizontalSwipeIsAllowed = true;
    private boolean verticalSwipeIsAllowed = true;
    private boolean horizontalSwipeInProgress = false;
    private boolean verticalSwipeInProgress = false;
    private boolean backPressEnabled = true;
    private boolean swipeUpAllowed = true;
    private boolean closeAllowed = true;


    private StoryListItemCoordinates currentCoordinates = null;

    private StoryReaderOpenState openState = StoryReaderOpenState.IDLE;

    private int currentPage = 0;
    private ReviewDialogState reviewDialogState = null;
    private ShareDataState shareDataState = null;
    private GoodsV1WidgetState goodsV1WidgetState = null;

    public StoryReaderState backPressEnabled(boolean backPressEnabled) {
        this.backPressEnabled = backPressEnabled;
        return this;
    }

    public StoryReaderState swipeUpAllowed(boolean swipeUpAllowed) {
        this.swipeUpAllowed = swipeUpAllowed;
        return this;
    }

    public StoryReaderState closeAllowed(boolean closeAllowed) {
        this.closeAllowed = closeAllowed;
        return this;
    }

    public StoryReaderState currentCoordinates(StoryListItemCoordinates currentCoordinates) {
        this.currentCoordinates = currentCoordinates;
        return this;
    }

    public StoryReaderState horizontalSwipeIsAllowed(boolean horizontalSwipeIsAllowed) {
        this.horizontalSwipeIsAllowed = horizontalSwipeIsAllowed;
        return this;
    }

    public StoryReaderState verticalSwipeIsAllowed(boolean verticalSwipeIsAllowed) {
        this.verticalSwipeIsAllowed = verticalSwipeIsAllowed;
        return this;
    }

    public StoryReaderState horizontalSwipeInProgress(boolean horizontalSwipeInProgress) {
        this.horizontalSwipeInProgress = horizontalSwipeInProgress;
        return this;
    }

    public StoryReaderState verticalSwipeInProgress(boolean verticalSwipeInProgress) {
        this.verticalSwipeInProgress = verticalSwipeInProgress;
        return this;
    }

    public StoryReaderState openState(StoryReaderOpenState openState) {
        this.openState = openState;
        return this;
    }

    public StoryReaderState currentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public StoryReaderState reviewDialogState(ReviewDialogState reviewDialogState) {
        this.reviewDialogState = reviewDialogState;
        return this;
    }

    public StoryReaderState shareDataState(ShareDataState shareDataState) {
        this.shareDataState = shareDataState;
        return this;
    }

    public StoryReaderState goodsV1WidgetState(GoodsV1WidgetState goodsV1WidgetState) {
        this.goodsV1WidgetState = goodsV1WidgetState;
        return this;
    }

    public StoryListItemCoordinates currentCoordinates() {
        return currentCoordinates;
    }

    public boolean horizontalSwipeIsAllowed() {
        return horizontalSwipeIsAllowed;
    }

    public boolean verticalSwipeIsAllowed() {
        return verticalSwipeIsAllowed;
    }

    public boolean horizontalSwipeInProgress() {
        return horizontalSwipeInProgress;
    }

    public boolean verticalSwipeInProgress() {
        return verticalSwipeInProgress;
    }

    public boolean backPressEnabled() {
        return backPressEnabled;
    }

    public boolean swipeUpAllowed() {
        return swipeUpAllowed;
    }

    public boolean closeAllowed() {
        return closeAllowed;
    }

    public StoryReaderOpenState openState() {
        return openState;
    }

    public int currentPage() {
        return currentPage;
    }

    public ReviewDialogState reviewDialogState() {
        return reviewDialogState;
    }

    public ShareDataState shareDataState() {
        return shareDataState;
    }

    public GoodsV1WidgetState goodsV1WidgetState() {
        return goodsV1WidgetState;
    }

    public StoryReaderState copy() {
        return new StoryReaderState()
                .openState(openState)
                .reviewDialogState(reviewDialogState)
                .goodsV1WidgetState(goodsV1WidgetState)
                .shareDataState(shareDataState)
                .currentPage(currentPage)
                .backPressEnabled(backPressEnabled)
                .swipeUpAllowed(swipeUpAllowed)
                .closeAllowed(closeAllowed)
                .currentCoordinates(currentCoordinates)
                .horizontalSwipeIsAllowed(horizontalSwipeIsAllowed)
                .horizontalSwipeInProgress(horizontalSwipeInProgress)
                .verticalSwipeInProgress(verticalSwipeInProgress)
                .verticalSwipeIsAllowed(verticalSwipeIsAllowed);
    }

}
