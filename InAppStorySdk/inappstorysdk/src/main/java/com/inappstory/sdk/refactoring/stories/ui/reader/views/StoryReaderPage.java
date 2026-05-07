package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import static com.inappstory.sdk.AppearanceManager.BOTTOM_END;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_START;
import static com.inappstory.sdk.AppearanceManager.TOP_END;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;
import static com.inappstory.sdk.AppearanceManager.TOP_START;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithoutStates;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.core.utils.observers.STETypeAndData;
import com.inappstory.sdk.refactoring.core.utils.stedata.ContentIdWithIndex;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.JsSendApiRequestResponse;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.LoadSlide;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.SetSoundStatus;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.StartSlide;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.StopSlide;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.StoriesSTEDataType;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderPageViewModel;
import com.inappstory.sdk.stories.api.models.ContentId;
import com.inappstory.sdk.stories.ui.views.IASWebViewClient;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.Objects;

public class StoryReaderPage extends FrameLayout implements Observer<Boolean> {
    StoryReaderPageLoader loader;
    StoryReaderPageContent content;
    StoryReaderPageTimeline timeline;
    StoryReaderPageButtons buttons;
    private boolean vmIsAttached;
    View topOffsetView;
    View bottomOffsetView;
    TouchFrameLayout closeButton;

    StoryReaderPageViewModel viewModel;

    Observer<STETypeAndData> singleTimeEvents = new Observer<STETypeAndData>() {
        @Override
        public void onUpdate(STETypeAndData newValue) {
            StoriesSTEDataType type;
            if (newValue.type() instanceof StoriesSTEDataType) {
                type = (StoriesSTEDataType) newValue.type();
            } else {
                return;
            }
            switch (type) {
                case CALL_TO_ACTION:
                    break;
                case JS_SEND_API_RESPONSE:
                    JsSendApiRequestResponse apiRequestResponse = (JsSendApiRequestResponse) newValue.data();
                    content.loadJsApiResponse(apiRequestResponse.result(), apiRequestResponse.cb());
                    break;
                case OPEN_STORY:
                    ContentIdWithIndex idWithIndex = (ContentIdWithIndex) newValue.data();
                    viewModel.openAnotherStory(
                            getContext(),
                            idWithIndex.id(),
                            idWithIndex.index()
                    );
                    break;
                case OPEN_GAME:
                    ContentId gameId = (ContentId) newValue.data();
                    viewModel.openGame(getContext(), gameId.id());
                    break;
                case AUTO_SLIDE_END:
                    content.stopSlide(false);
                    content.autoSlideEnd();
                    break;
                case FREEZE_UI:
                    break;
                case UNFREEZE_UI:
                    break;
                case RENDER_READY:
                    content.setClientVariables(viewModel.options());
                    break;
                case LOAD_SLIDE:
                    LoadSlide loadSlideData = (LoadSlide) newValue.data();
                    viewModel.clearTemporaryLayoutAndSlide();
                    content.layoutAndSlide(loadSlideData.layout(), loadSlideData.slide());
                    break;
                case START_SLIDE:
                    StartSlide startSlideData = (StartSlide) newValue.data();
                    content.startSlide(startSlideData.soundOn());
                    break;
                case SET_SOUND_STATUS:
                    SetSoundStatus updatedSoundStatus = (SetSoundStatus) newValue.data();
                    content.changeSoundStatus(updatedSoundStatus.soundOn());
                    break;
                case RESTART_SLIDE:
                    StartSlide restartSlideData = (StartSlide) newValue.data();
                    content.restartSlide(restartSlideData.soundOn());
                    break;
                case PAUSE_SLIDE:
                    content.pauseSlide();
                    break;
                case RESUME_SLIDE:
                    content.resumeSlide();
                    break;
                case STOP_SLIDE:
                    StopSlide stopSlide = (StopSlide) newValue.data();
                    content.stopSlide(stopSlide.prepareForRestart());
                    break;
            }
        }
    };

    public void viewModel(StoryReaderPageViewModel viewModel) {
        this.viewModel = viewModel;
        attachViewModelToViews();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!vmIsAttached) return;
        vmIsAttached = false;
        if (viewModel != null) {
            viewModel.removeCloseSubscriber(this);
            viewModel.removeButtonsStateSubscriber(buttons);
            viewModel.removeLoaderStateSubscriber(loader);
            viewModel.removeTimelineStateSubscriber(timeline);
            viewModel.singleTimeEvents().unsubscribe(singleTimeEvents);
            viewModel.destroy();
        }
        content.viewModel(null);
    }

    public void destroyView() {
        Log.e("destroyReader", "destroyView " + viewModel);
        if (viewModel != null)
            viewModel.destroy();
    }

    public void pause() {
    }

    public void resume() {
    }

    private void attachViewModelToViews() {
        if (vmIsAttached) return;
        vmIsAttached = true;
        if (viewModel != null) {
            content.viewModel(viewModel);
            buttons.viewModel(viewModel);
            loader.viewModel(viewModel);
            viewModel.addTimelineStateSubscriber(timeline);
            viewModel.addCloseSubscriber(this);
            viewModel.singleTimeEvents().subscribe(singleTimeEvents);
            viewModel.checkForTemporaryData();
        }
    }

    public void setOffsets(int top, int bottom) {

    }

    public StoryReaderPage(@NonNull Context context) {
        super(context);
        init(context);
    }


    public StoryReaderPage(
            @NonNull Context context,
            StoryReaderPageAppearance pageAppearance
    ) {
        super(context);
        this.pageAppearance = pageAppearance;
        init(context);
    }

    private StoryReaderPageAppearance pageAppearance;

    public StoryReaderPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StoryReaderPage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        loader = new StoryReaderPageLoader(context);

        content = new StoryReaderPageContent(context);

        buttons = new StoryReaderPageButtons(context);

        timeline = new StoryReaderPageTimeline(context);
        timeline.setId(R.id.ias_timeline);

        topOffsetView = new View(context);

        bottomOffsetView = new View(context);

        content.setWebViewClient(new IASWebViewClient());

        closeButton = new TouchFrameLayout(context);
        closeButton.setId(R.id.ias_close_button);
    }

    public void measureViews() {
        if (viewModel == null || pageAppearance == null) return;
        boolean isFullscreen = viewModel.storyReaderPageState().storyItem().fullscreen();
        createFullscreenPage();
    }

    private void createFullscreenPage() {
        content.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        addView(content);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        View space = new View(getContext());
        space.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1f
                )
        );
        topOffsetView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Math.max(
                                0,
                                pageAppearance.screenPosition.safeAreaTop -
                                        pageAppearance.screenPosition.readerContainerTop
                        )
                )
        );
        bottomOffsetView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Math.max(
                                0,
                                pageAppearance.screenPosition.safeAreaBottom -
                                        (pageAppearance.screenPosition.screenHeight -
                                                pageAppearance.screenPosition.readerContainerBottom)
                        )
                )
        );
        buttons.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Sizes.dpToPxExt(60, getContext())
                )
        );
        linearLayout.addView(topOffsetView);


        final CustomIconWithoutStates customCloseIconInterface = AppearanceManager.
                getCommonInstance().
                csCustomIcons().
                closeIcon();
        int maxSize = Sizes.dpToPxExt(30, getContext());
        final View customCloseView = customCloseIconInterface.createIconView(
                getContext(),
                new SizeF(maxSize, maxSize)
        );

        closeButton.setLayoutParams(new RelativeLayout.LayoutParams(
                maxSize,
                maxSize)
        );
        closeButton.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customCloseIconInterface.touchEvent(customCloseView, event);
                return false;
            }
        });
        closeButton.addView(customCloseView);

        customCloseView.setClickable(false);
        RelativeLayout rl = createTimelineContainer(getContext());
        rl.setBackgroundColor(Color.BLUE);
        linearLayout.addView(rl);
        linearLayout.addView(space);
        linearLayout.addView(buttons);
        linearLayout.addView(bottomOffsetView);


        addView(linearLayout);
    }


    private RelativeLayout createTimelineContainer(Context context) {
        RelativeLayout timelineContainer = new RelativeLayout(context);
        RelativeLayout.LayoutParams tclp = new RelativeLayout.LayoutParams(MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int offset = 0;// Sizes.dpToPxExt(Math.max(0, appearanceSettings.csReaderRadius() - 16), getContext()) / 2;
        tclp.setMargins(offset, Sizes.dpToPxExt(8, getContext()) + offset, offset, 0);
        timelineContainer.setLayoutParams(tclp);
        timelineContainer.setId(R.id.ias_timeline_container);
        timelineContainer.setMinimumHeight(Sizes.dpToPxExt(30, getContext()));
        timelineContainer.setElevation(20);
        timeline.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT,
                Sizes.dpToPxExt(3, getContext())));
        final CustomIconWithoutStates customCloseIconInterface = AppearanceManager.
                getCommonInstance().
                csCustomIcons().
                closeIcon();
        int maxSize = Sizes.dpToPxExt(30, getContext());
        final View customCloseView = customCloseIconInterface.createIconView(context, new SizeF(maxSize, maxSize));
        closeButton.setLayoutParams(new RelativeLayout.LayoutParams(
                maxSize,
                maxSize)
        );
        closeButton.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customCloseIconInterface.touchEvent(customCloseView, event);
                return false;
            }
        });
        closeButton.addView(customCloseView);
        customCloseView.setClickable(false);


        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) closeButton.getLayoutParams();
        RelativeLayout.LayoutParams storiesProgressViewLP = (RelativeLayout.LayoutParams) timeline.getLayoutParams();
        int cp = viewModel.readerAppearanceSettings().csClosePosition();
        int viewsMargin = Sizes.dpToPxExt(8, getContext());
        storiesProgressViewLP.leftMargin =
                storiesProgressViewLP.rightMargin = viewsMargin;

        switch (cp) {
            case TOP_RIGHT:
                layoutParams.rightMargin = viewsMargin;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.LEFT_OF, closeButton.getId());
                break;
            case TOP_LEFT:
                layoutParams.leftMargin = viewsMargin;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.RIGHT_OF, closeButton.getId());
                break;
            case BOTTOM_RIGHT:
                layoutParams.rightMargin = viewsMargin;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                storiesProgressViewLP.topMargin = viewsMargin;
                layoutParams.topMargin = viewsMargin;
                break;
            case BOTTOM_LEFT:
                layoutParams.leftMargin = viewsMargin;
                storiesProgressViewLP.topMargin = viewsMargin;
                layoutParams.topMargin = viewsMargin;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                break;
            case TOP_START:
                layoutParams.setMarginStart(viewsMargin);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.END_OF, closeButton.getId());
                break;
            case TOP_END:
                layoutParams.setMarginEnd(viewsMargin);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.START_OF, closeButton.getId());
                break;
            case BOTTOM_START:
                layoutParams.setMarginStart(viewsMargin);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                storiesProgressViewLP.topMargin = viewsMargin;
                layoutParams.topMargin = viewsMargin;
                break;
            case BOTTOM_END:
                layoutParams.setMarginEnd(viewsMargin);
                storiesProgressViewLP.topMargin = viewsMargin;
                layoutParams.topMargin = viewsMargin;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                break;
        }
        closeButton.setLayoutParams(layoutParams);


        timelineContainer.addView(timeline);
        timelineContainer.addView(closeButton);


        return timelineContainer;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (viewModel != null)
            attachViewModelToViews();
    }

    @Override
    public void onUpdate(Boolean isCloseable) {
        if (Objects.equals(isCloseable, true)) {
            closeButton.setVisibility(VISIBLE);
        } else {
            closeButton.setVisibility(GONE);
        }
    }
}
