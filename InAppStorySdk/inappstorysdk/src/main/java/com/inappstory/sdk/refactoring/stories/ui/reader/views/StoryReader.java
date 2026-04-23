package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.shared.ui.ContainerProvider;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.GoodsV1WidgetState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.ReviewDialogState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.ShareDataState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderOpenState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderViewModel;

public class StoryReader extends FrameLayout implements Observer<StoryReaderState> {
    private ContainerProvider shareContainer;
    private ContainerProvider goodsContainer;
    private ContainerProvider reviewContainer;
    private StoryReaderPager pager;
    private StoryReaderState currentValue = null;

    public void shareContainer(ContainerProvider shareContainer) {
        this.shareContainer = shareContainer;
    }

    public void goodsContainer(ContainerProvider goodsContainer) {
        this.goodsContainer = goodsContainer;
    }

    public void reviewContainer(ContainerProvider reviewContainer) {
        this.reviewContainer = reviewContainer;
    }

    public void viewModel(StoryReaderViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private StoryReaderViewModel viewModel;

    private void init(@NonNull Context context) {

    }

    public StoryReader(@NonNull Context context) {
        super(context);
        init(context);
    }

    public StoryReader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StoryReader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void pause() {

    }

    public void resume() {

    }

    public void updateOpenState(StoryReaderOpenState newState) {

    }

    public void updateShareViewIfNecessary(
            ShareDataState oldState,
            ShareDataState newState
    ) {
        if (oldState == null && newState != null) {

        } else if (newState == null && oldState != null) {

        }
    }

    public void updateReviewViewIfNecessary(
            ReviewDialogState oldState,
            ReviewDialogState newState
    ) {
        if (oldState == null && newState != null) {

        } else if (newState == null && oldState != null) {

        }
    }

    public void updateGoodsV1ViewIfNecessary(
            GoodsV1WidgetState oldState,
            GoodsV1WidgetState newState
    ) {
        if (oldState == null && newState != null) {

        } else if (newState == null && oldState != null) {

        }
    }

    private void pageSelected(int newPage) {

    }

    @Override
    public void onUpdate(StoryReaderState newValue) {
        if (newValue.openState() != currentValue.openState()) {
            updateOpenState(newValue.openState());
        }
        if (currentValue.currentPage() != newValue.currentPage()) {
            pageSelected(newValue.currentPage());
        }
        updateShareViewIfNecessary(
                currentValue.shareDataState(),
                newValue.shareDataState()
        );
        updateReviewViewIfNecessary(
                currentValue.reviewDialogState(),
                newValue.reviewDialogState()
        );
        updateGoodsV1ViewIfNecessary(
                currentValue.goodsV1WidgetState(),
                newValue.goodsV1WidgetState()
        );
        currentValue = newValue;
    }
}
