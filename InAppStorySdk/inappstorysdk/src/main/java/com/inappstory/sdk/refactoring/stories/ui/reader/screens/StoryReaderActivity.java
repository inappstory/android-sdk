package com.inappstory.sdk.refactoring.stories.ui.reader.screens;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.ui.screens.IASActivity;
import com.inappstory.sdk.refactoring.stories.ui.reader.views.StoryReader;

public class StoryReaderActivity extends IASActivity implements BaseStoryReaderContainer {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cs_story_reader_activity);
        if (Build.VERSION.SDK_INT >= 33) {
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, callback);
        }
        final StoryReader storyReader = ((StoryReader) findViewById(R.id.story_reader));
        storyReader.setHostContainer(this);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                storyReader.viewModel(
                        core.screensManager().storyReaderViewModel()
                );
            }
        });

    }

    @Override
    public void onBackPressed() {
        StoryReader storyReader = ((StoryReader) findViewById(R.id.story_reader));
        if (storyReader != null) {
            storyReader.handleBackPress();
        }
    }

    @Override
    protected void onDestroy() {
        StoryReader storyReader = ((StoryReader) findViewById(R.id.story_reader));
        if (storyReader != null) {
            storyReader.setHostContainer(null);
            storyReader.unsubscribe();
        }
        super.onDestroy();
    }

    public void forceFinish() {
        StoryReader storyReader = ((StoryReader) findViewById(R.id.story_reader));
        if (storyReader != null) {
            storyReader.forceFinish();
        }
    }

    @Override
    public void finish() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void close() {
        finish();
    }
}
