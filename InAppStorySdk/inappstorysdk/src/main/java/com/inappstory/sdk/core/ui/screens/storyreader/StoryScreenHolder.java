package com.inappstory.sdk.core.ui.screens.storyreader;

import static java.util.UUID.randomUUID;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.ui.screens.holder.AbstractScreenHolder;
import com.inappstory.sdk.core.ui.screens.holder.IOverlapContainerData;
import com.inappstory.sdk.core.ui.screens.holder.IOverlapContainerHolder;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.goods.GoodsWidgetFragment;
import com.inappstory.sdk.stories.ui.reader.ActiveStoryItem;
import com.inappstory.sdk.stories.ui.reader.OverlapFragment;

public class StoryScreenHolder extends AbstractScreenHolder<BaseStoryScreen, LaunchStoryScreenData> implements IOverlapContainerHolder {
    private final ShareProcessHandler shareProcessHandler;
    private final IASCore core;

    public StoryScreenHolder(
            IASCore core,
            ShareProcessHandler shareProcessHandler
    ) {
        this.core = core;
        this.shareProcessHandler = shareProcessHandler;
    }

    public void closeScreenWithAction(int action) {
        BaseStoryScreen screen = getScreen();
        if (screen != null)
            screen.closeWithAction(action);
    }


    public ActiveStoryItem activeStoryItem() {
        return activeStoryItem;
    }

    public void activeStoryItem(ActiveStoryItem activeStoryItem) {
        this.activeStoryItem = activeStoryItem;
    }

    public StoryItemCoordinates coordinates() {
        return coordinates;
    }

    public void coordinates(StoryItemCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void clearCoordinates() {
        coordinates = null;
        activeStoryItem = null;
    }

    private ActiveStoryItem activeStoryItem = null;
    private StoryItemCoordinates coordinates = null;

    @Override
    public void openShareOverlapContainer(IOverlapContainerData data, FragmentManager fragmentManager, OverlapFragmentObserver observer) {
        if (data instanceof StoryReaderOverlapContainerDataForShare) {
            StoryReaderOverlapContainerDataForShare storyReaderShareData = (StoryReaderOverlapContainerDataForShare) data;
            try {
                shareProcessHandler.overlapFragmentObserver(observer);
                shareProcessHandler.shareListener(storyReaderShareData.shareListener);
                OverlapFragment overlapFragment = new OverlapFragment();
                Bundle bundle = new Bundle();
                bundle.putString("slidePayload", storyReaderShareData.slidePayload);
                bundle.putInt("storyId", storyReaderShareData.storyId);
                bundle.putInt("slideIndex", storyReaderShareData.slideIndex);
                bundle.putSerializable("shareData", storyReaderShareData.shareData);
                overlapFragment.setArguments(bundle);
                FragmentTransaction t = fragmentManager.beginTransaction()
                        .replace(R.id.ias_outer_top_container, overlapFragment);
                t.addToBackStack("OverlapFragment");
                t.commit();
            } catch (IllegalStateException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void openGoodsOverlapContainer(String skusString, String widgetId, SlideData slideData) {
        final String localTaskId;
        if (widgetId != null) localTaskId = widgetId;
        else localTaskId = randomUUID().toString();
        final FragmentManager fragmentManager = getScreen().getScreenFragmentManager();
        if (slideData != null) {
            core.statistic().v2().sendGoodsOpen(
                    slideData.story.id,
                    slideData.index,
                    widgetId,
                    slideData.story.feed
            );
        }
        try {
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
        } catch (Exception e) {

        }
    }
}
