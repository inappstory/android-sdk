package com.inappstory.sdk.core.ui.screens.gamereader;


import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.ui.screens.holder.AbstractScreenHolder;
import com.inappstory.sdk.core.ui.screens.holder.IOverlapContainerData;
import com.inappstory.sdk.core.ui.screens.holder.IOverlapContainerHolder;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.stories.events.GameCompleteEventObserver;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.reader.OverlapFragment;

import java.util.HashMap;

public class GameScreenHolder extends AbstractScreenHolder<BaseGameScreen, LaunchGameScreenData>  implements IOverlapContainerHolder {
    private final ShareProcessHandler shareProcessHandler;

    HashMap<String, GameCompleteEventObserver> gameObservables = new HashMap<>();

    public GameCompleteEventObserver getGameObserver(String id) {
        return gameObservables.get(id);
    }

    public void putGameObserver(String id, GameCompleteEventObserver observer) {
        gameObservables.put(id, observer);
    }

    public void removeGameObserver(String id) {
        gameObservables.remove(id);
    }

    public GameScreenHolder(ShareProcessHandler shareProcessHandler) {
        this.shareProcessHandler = shareProcessHandler;
    }

    @Override
    public void openShareOverlapContainer(IOverlapContainerData data, FragmentManager fragmentManager, OverlapFragmentObserver observer) {
        if (data instanceof GameReaderOverlapContainerDataForShare) {
            GameReaderOverlapContainerDataForShare storyReaderShareData = (GameReaderOverlapContainerDataForShare) data;
            try {
                shareProcessHandler.overlapFragmentObserver(observer);
                shareProcessHandler.shareListener(storyReaderShareData.shareListener);
                OverlapFragment overlapFragment = new OverlapFragment();
                Bundle bundle = new Bundle();
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

    }
}
