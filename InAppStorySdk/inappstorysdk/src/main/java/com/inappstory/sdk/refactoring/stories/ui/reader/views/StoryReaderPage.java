package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        }
        content.viewModel(null);
    }

    public void destroyView() {
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
        topOffsetView = new View(context);
        bottomOffsetView = new View(context);
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
        timeline.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Sizes.dpToPxExt(3, getContext())
                )
        );
        buttons.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Sizes.dpToPxExt(60, getContext())
                )
        );
        linearLayout.addView(topOffsetView);
        linearLayout.addView(timeline);
        linearLayout.addView(space);
        linearLayout.addView(buttons);
        linearLayout.addView(bottomOffsetView);
        addView(linearLayout);
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
