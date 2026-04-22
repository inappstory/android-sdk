package com.inappstory.sdk.refactoring.stories.ui.reader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.core.utils.observers.STETypeAndData;
import com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents.StoriesSTEDataType;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderPageViewModel;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;

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
                case SLIDE_IN_CACHE:
                    break;
                case JS_SEND_API_REQUEST:
                    break;
                case JS_SEND_API_RESPONSE:
                    break;
                case OPEN_STORY:
                    break;
                case CLOSE_READER:
                    break;
                case OPEN_GAME:
                    break;
                case AUTO_SLIDE_END:
                    break;
                case FREEZE_UI:
                    break;
                case UNFREEZE_UI:
                    break;
                case RENDER_READY:
                    content.setClientVariables(viewModel.options());
                    break;
                case LOAD_SLIDE:
                    break;
                case UPDATE_TIMELINE:
                    break;
                case START_SLIDE:
                    break;
                case RESTART_SLIDE:
                    break;
                case PAUSE_SLIDE:
                    break;
                case RESUME_SLIDE:
                    break;
                case STOP_SLIDE:
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
        viewModel.removeCloseSubscriber(this);
        viewModel.removeButtonsStateSubscriber(buttons);
        viewModel.removeLoaderStateSubscriber(loader);
        viewModel.removeTimelineStateSubscriber(timeline);
        viewModel.singleTimeEvents().unsubscribe(singleTimeEvents);
        content.viewModel(null);
    }

    private void attachViewModelToViews() {
        if (vmIsAttached) return;
        vmIsAttached = true;
        content.viewModel(viewModel);
        buttons.viewModel(viewModel);
        loader.viewModel(viewModel);
        viewModel.addTimelineStateSubscriber(timeline);
        viewModel.addCloseSubscriber(this);
        viewModel.singleTimeEvents().subscribe(singleTimeEvents);
    }

    public void setOffsets(int top, int bottom) {

    }

    public StoryReaderPage(@NonNull Context context) {
        super(context);
        init(context);
    }

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
