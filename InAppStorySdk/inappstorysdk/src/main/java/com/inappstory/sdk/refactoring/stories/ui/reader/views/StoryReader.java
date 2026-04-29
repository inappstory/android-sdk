package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.elasticview.DraggableElasticLayout;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.shared.ui.ContainerProvider;
import com.inappstory.sdk.refactoring.stories.ui.reader.screens.BaseStoryReaderContainer;
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
    DraggableElasticLayout elasticLayout;
    BaseStoryReaderContainer hostContainer;

    View blockView;
    View readerBackground;
    View animatedContainer;

    public void shareContainer(ContainerProvider shareContainer) {
        this.shareContainer = shareContainer;
    }

    public void goodsContainer(ContainerProvider goodsContainer) { //?
        this.goodsContainer = goodsContainer;
    }

    public void reviewContainer(ContainerProvider reviewContainer) {
        this.reviewContainer = reviewContainer;
    }

    public void setHostContainer(BaseStoryReaderContainer container) {
        this.hostContainer = container;
    }

    public void viewModel(StoryReaderViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.addSubscriber(this);
    }

    public void unsubscribe() {
        if (viewModel != null) {
            viewModel.removeSubscriber(this);
        }
    }

    public void forceFinish() {
        viewModel.updateOpenState(StoryReaderOpenState.FORCE_CLOSING);
    }

    public void clearViewModel() {
        if (viewModel != null) {
            viewModel.cleanReaderData();
        }
    }

    private StoryReaderViewModel viewModel;

    private void init(@NonNull Context context) {
        inflate(context, R.layout.cs_story_reader, this);
        pager = findViewById(R.id.ias_stories_reader_pager);
        elasticLayout = findViewById(R.id.ias_stories_reader_scroll_container);
        reviewContainer = new ContainerProvider()
                .layout(findViewById(R.id.ias_stories_reader_review_container));
        FrameLayout extraContainer = findViewById(R.id.ias_stories_reader_extra_container);
        shareContainer = new ContainerProvider()
                .layout(extraContainer);
        goodsContainer = new ContainerProvider()
                .layout(extraContainer);
        blockView = findViewById(R.id.ias_stories_reader_block_view);
        readerBackground = findViewById(R.id.ias_stories_reader_background);
        animatedContainer = findViewById(R.id.ias_stories_reader_animated_container);
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

    public void handleBackPress() {
        if (viewModel != null) {
            viewModel.handleBackPress();
        }
    }

    public void pause() {
        pauseCurrentPage();
    }

    public void resume() {
        resumeCurrentPage();
    }


    private void pauseCurrentPage() {
        StoryReaderPage page = getCurrentPage();
        if (page != null) page.pause();
    }

    private StoryReaderPage getCurrentPage() {
        if (pager == null) return null;
        String currentItemTag = StoryReaderPagerAdapter.getTagByPosition(pager.getCurrentItem());
        return findViewWithTag(currentItemTag);
    }

    private void resumeCurrentPage() {
        StoryReaderPage page = getCurrentPage();
        if (page != null) page.resume();
    }

    public void updateOpenState(StoryReaderOpenState newState) {
        switch (newState) {
            case CLOSED:
                if (hostContainer != null)
                    hostContainer.close();
                break;
            case OPENING:
                break;
            case OPENED:
                break;
            case CLOSING:
                break;
            case FORCE_CLOSING:
                break;
        }
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
