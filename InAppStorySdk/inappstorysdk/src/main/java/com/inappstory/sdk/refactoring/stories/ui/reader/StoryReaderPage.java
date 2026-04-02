package com.inappstory.sdk.refactoring.stories.ui.reader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageState;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderPageViewModel;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;

public class StoryReaderPage extends FrameLayout {
    StoryReaderPageLoader loader;
    StoryReaderPageContent content;
    StoryReaderPageTimeline timeline;
    StoryReaderPageButtons buttons;
    View topOffsetView;
    View bottomOffsetView;
    TouchFrameLayout closeButton;


    StoryReaderPageViewModel viewModel;

    public void viewModel(StoryReaderPageViewModel viewModel) {
        this.viewModel = viewModel;
        if (buttons != null) buttons.viewModel(viewModel);
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

    }

}
