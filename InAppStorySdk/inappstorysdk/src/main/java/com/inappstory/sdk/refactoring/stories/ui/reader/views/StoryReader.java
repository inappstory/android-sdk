package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.ui.widgets.elasticview.DraggableElasticLayout;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.shared.ui.ContainerProvider;
import com.inappstory.sdk.refactoring.stories.data.contracts.IStoryListItem;
import com.inappstory.sdk.refactoring.stories.ui.list.states.StoryListItemCoordinates;
import com.inappstory.sdk.refactoring.stories.ui.reader.screens.BaseStoryReaderContainer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.GoodsV1WidgetState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.ReviewDialogState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.ShareDataState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderOpenState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderState;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderViewModel;
import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;
import com.inappstory.sdk.stories.ui.reader.animations.DisabledReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.FadeReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.HandlerAnimatorListenerAdapter;
import com.inappstory.sdk.stories.ui.reader.animations.PopupReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ZoomReaderCenterAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ZoomReaderFromCellAnimation;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoryReader extends FrameLayout implements Observer<StoryReaderState>, ViewPager.OnPageChangeListener {
    private ContainerProvider shareContainer;
    private ContainerProvider goodsContainer;
    private ContainerProvider reviewContainer;
    private StoryReaderPager pager;
    private StoryReaderFakePage fakePage;
    private StoryReaderState currentValue = new StoryReaderState();
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
        int lastIndex = viewModel.pageSlideIndexes.get(viewModel.readerState().currentPage());
        IStoryListItem storyListItem = viewModel.core()
                .storyRepository()
                .getLocalStoryListItem(
                        viewModel.readerImmutableState().storiesIds().get(
                                viewModel.readerState().currentPage()
                        )
                );
        fakePage.setPageState(
                new StoryReaderPageAppearance(
                        viewModel.appearanceSettings.csHasLike(),
                        viewModel.appearanceSettings.csHasFavorite(),
                        viewModel.appearanceSettings.csHasShare(),
                        viewModel.appearanceSettings.csCloseOnSwipe(),
                        viewModel.appearanceSettings.csCloseOnSwipe(),
                        viewModel.appearanceSettings.csClosePosition(),
                        viewModel.appearanceSettings.csReaderBackgroundColor(),
                        viewModel.appearanceSettings.csTimerGradientEnable(),
                        viewModel.appearanceSettings.csTimerGradient(),
                        new ScreenPosition()
                ),
                storyListItem,
                lastIndex);
        post(new Runnable() {
            @Override
            public void run() {
                final Rect readerContainer = new Rect();
                getGlobalVisibleRect(readerContainer);
                viewModel.readerImmutableState().readerFrame(readerContainer);
            }
        });
        viewModel.addSubscriber(this);
        pager.viewModel(viewModel);
    }

    public void unsubscribe() {
        if (viewModel != null) {
            viewModel.destroyPages();
            viewModel.removeSubscriber(this);
            viewModel.updateOpenState(StoryReaderOpenState.IDLE);
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
        fakePage = findViewById(R.id.ias_stories_reader_fake_page);
        elasticLayout = findViewById(R.id.ias_stories_reader_draggable_frame);
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


    private ReaderAnimation getStartAnimation() {
        if (viewModel == null || viewModel.appearanceSettings == null)
            return new DisabledReaderAnimation().setAnimations(true);
        Point screenSize = Sizes.getScreenSize(getContext());
        switch (viewModel.appearanceSettings.csStoryReaderPresentationStyle()) {
            case AppearanceManager.DISABLE:
                return new DisabledReaderAnimation().setAnimations(true);
            case AppearanceManager.FADE:
                return new FadeReaderAnimation(animatedContainer).setAnimations(true);
            case AppearanceManager.POPUP:
                return new PopupReaderAnimation(animatedContainer, screenSize.y, 0f).setAnimations(true);
            default:
                final StoryListItemCoordinates[] coordinates = {null};
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        coordinates[0] = viewModel.readerState().currentCoordinates();
                    }
                });
                float pivotX = -screenSize.x / 2f;
                float pivotY = -screenSize.y / 2f;
                if (coordinates[0] != null) {
                    pivotX += coordinates[0].x();
                    pivotY += coordinates[0].y();
                    return new ZoomReaderFromCellAnimation(animatedContainer,
                            pivotX,
                            pivotY
                    ).setAnimations(true);
                } else {
                    return new ZoomReaderCenterAnimation(animatedContainer,
                            -pivotX,
                            -pivotY
                    ).setAnimations(true);
                }
        }
    }

    private ReaderAnimation getFinishAnimation() {
        if (viewModel == null || viewModel.appearanceSettings == null)
            return new DisabledReaderAnimation().setAnimations(true);
        Point screenSize = Sizes.getScreenSize(getContext());
        switch (viewModel.appearanceSettings.csStoryReaderPresentationStyle()) {
            case AppearanceManager.DISABLE:
                return new DisabledReaderAnimation().setAnimations(false);
            case AppearanceManager.FADE:
                return new FadeReaderAnimation(animatedContainer).setAnimations(false);
            case AppearanceManager.POPUP:
                return new PopupReaderAnimation(
                        animatedContainer,
                        elasticLayout.getY(),
                        screenSize.y
                ).setAnimations(false);
            default:
                final StoryListItemCoordinates[] coordinates = {null};
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        coordinates[0] = viewModel.readerState().currentCoordinates();
                    }
                });
                float pivotX = -screenSize.x / 2f;
                float pivotY = -screenSize.y / 2f;
                if (coordinates[0] != null) {
                    pivotX += coordinates[0].x();
                    pivotY += coordinates[0].y();
                    return new ZoomReaderFromCellAnimation(animatedContainer,
                            pivotX,
                            pivotY
                    ).setAnimations(false);
                } else {
                    return new ZoomReaderCenterAnimation(animatedContainer,
                            -pivotX,
                            -pivotY
                    ).setAnimations(false);
                }

        }
    }

    public void updateOpenState(StoryReaderOpenState newState) {
        switch (newState) {
            case CLOSED:
                if (hostContainer != null)
                    hostContainer.close();
                break;
            case OPENING:
                getStartAnimation().setListener(new HandlerAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart() {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                animatedContainer.setVisibility(VISIBLE);
                            }
                        }, 50);
                        super.onAnimationStart();
                    }

                    @Override
                    public void onAnimationEnd() {
                        super.onAnimationEnd();
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateOpenState(StoryReaderOpenState.OPENED);
                            }
                        }, 50);

                    }
                }).start();
                break;
            case OPENED:
                pager.transformAnimation(AppearanceManager.ANIMATION_CUBE);

                pager.setAdapter(
                        new StoryReaderPagerAdapter(
                                viewModel,
                                new StoryReaderPageAppearance(
                                        viewModel.appearanceSettings.csHasLike(),
                                        viewModel.appearanceSettings.csHasFavorite(),
                                        viewModel.appearanceSettings.csHasShare(),
                                        viewModel.appearanceSettings.csCloseOnSwipe(),
                                        viewModel.appearanceSettings.csCloseOnSwipe(),
                                        viewModel.appearanceSettings.csClosePosition(),
                                        viewModel.appearanceSettings.csReaderBackgroundColor(),
                                        viewModel.appearanceSettings.csTimerGradientEnable(),
                                        viewModel.appearanceSettings.csTimerGradient(),
                                        new ScreenPosition()
                                )
                        )
                );
                try {
                    pager.removeOnPageChangeListener(this);
                } catch (Exception e) {

                }
                pager.addOnPageChangeListener(this);
                if (viewModel != null) {
                    pager.setCurrentItem(viewModel.readerState().currentPredictedPage());
                    viewModel.pagerPageSelected(viewModel.readerState().currentPredictedPage());
                }
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fakePage.setVisibility(GONE);
                    }
                }, 300);
                break;
            case CLOSING:
                getFinishAnimation().setListener(new HandlerAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd() {
                        super.onAnimationEnd();
                        updateOpenState(StoryReaderOpenState.CLOSED);
                    }
                }).start();
                break;
            case FORCE_CLOSING:
                updateOpenState(StoryReaderOpenState.CLOSED);
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
        final StoryReaderState oldState = currentValue;
        post(new Runnable() {
            @Override
            public void run() {
                if (newValue.openState() != oldState.openState()) {
                    updateOpenState(newValue.openState());
                }
                if (oldState.currentPage() != newValue.currentPredictedPage()) {
                    if (pager.getAdapter() != null)
                        pager.setCurrentItem(newValue.currentPredictedPage());
                }
                updateShareViewIfNecessary(
                        oldState.shareDataState(),
                        newValue.shareDataState()
                );
                updateReviewViewIfNecessary(
                        oldState.reviewDialogState(),
                        newValue.reviewDialogState()
                );
                updateGoodsV1ViewIfNecessary(
                        oldState.goodsV1WidgetState(),
                        newValue.goodsV1WidgetState()
                );
            }
        });

        currentValue = newValue;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (viewModel != null)
            viewModel.pagerPageScrolled(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {

                }
                if (viewModel != null)
                    viewModel.pagerPageSelected(position);
            }
        }).start();

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (viewModel != null)
            viewModel.pagerPageScrollStateChanged(state);
    }
}
