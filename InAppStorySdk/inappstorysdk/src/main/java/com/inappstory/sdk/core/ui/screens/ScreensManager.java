package com.inappstory.sdk.core.ui.screens;

import static java.util.UUID.randomUUID;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.screens.gamereader.BaseGameScreen;
import com.inappstory.sdk.core.ui.screens.outsideapi.CloseUgcReaderCallback;
import com.inappstory.sdk.game.reader.GameStoryData;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEventObserver;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.GameReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.InAppMessageReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.goods.GoodsWidgetFragment;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;

import java.util.HashMap;

public class ScreensManager {

    private ScreensManager() {

    }

    private final static ScreensManager INSTANCE = new ScreensManager();

    public static ScreensManager getInstance() {
        return INSTANCE;
    }



    public void showGoods(
            String skusString,
            final BaseStoryScreen readerScreen,
            final String widgetId,
            final SlideData slideData
    ) {
        final String localTaskId;
        if (widgetId != null) localTaskId = widgetId;
        else localTaskId = randomUUID().toString();
        final FragmentManager fragmentManager = readerScreen.getScreenFragmentManager();
        if (StatisticManager.getInstance() != null && slideData != null) {
            StatisticManager.getInstance().sendGoodsOpen(slideData.story.id,
                    slideData.index, widgetId, slideData.story.feed);
        }

        GoodsWidgetFragment fragment = new GoodsWidgetFragment();
        Bundle args = new Bundle();
        args.putString("localTaskId", localTaskId);
        args.putSerializable("slideData", slideData);
        args.putString("widgetId", widgetId);
        args.putString("skusString", skusString);
        fragment.setArguments(args);
        FragmentTransaction t = fragmentManager.beginTransaction()
                .replace(R.id.ias_outer_top_container, fragment);
        t.addToBackStack("GoodsWidgetFragment");
        t.commit();

    }
}
