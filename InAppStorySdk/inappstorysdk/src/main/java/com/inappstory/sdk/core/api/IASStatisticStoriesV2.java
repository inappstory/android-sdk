package com.inappstory.sdk.core.api;

import com.inappstory.sdk.stories.api.models.SlideLayerKey;

import java.util.List;
import java.util.Map;

public interface IASStatisticStoriesV2 extends StatDisabled {

    void cleanTasks();

    void pauseStoryEvent(boolean withBg);

    void resumeStoryEvent(boolean withBg);

    void cleanFakeEvents();

    void sendViewStory(final int storyId, final String whence, final String feed);

    void sendGoodsOpen(final int storyId,
                       final int slideIndex,
                       final String widgetId,
                       final String feed);

    void sendGoodsClick(final int i, final int si,
                        final String wi, final String sku,
                        final String feed);

    void sendViewStory(List<Integer> ids, final String w,
                       final String feed);

    void sendOpenStory(final int i, final String w,
                       final String feed);

    void sendCloseStory(final int i,
                        final String c,
                        final Integer si,
                        final Integer st,
                        final String feed);

    void sendCurrentState();

    void createCurrentState(final int stId,
                            final int ind,
                            final String feed);

    void addFakeEvents(final int i,
                       final Integer si,
                       final Integer st,
                       final String feed);

    void sendDeeplinkStory(final int i,
                           String link,
                           final String feed);

    void sendClickLink(int storyId);

    void sendLikeStory(final int i,
                       final int si,
                       final String feed);

    void sendDislikeStory(final int i,
                          final int si,
                          final String feed);

    void sendFavoriteStory(final int i,
                           final int si,
                           final String feed);

    void sendViewSlide(final int i,
                       final int si,
                       final Long t,
                       final String feed);

    void sendShareStory(final int i,
                        final int si,
                        int mode,
                        final String feed);

    void sendStoryWidgetEvent(
            final String name,
            final String data,
            final String feed,
            boolean forceSend
    );

    void sendSlideScrollEvents(String feed);

    Map<SlideLayerKey, Float> getScrollEvents();

    void setScrollEvents(Map<SlideLayerKey, Float> scrollEvents);

    void addScrollEvent(
            SlideLayerKey key,
            float value
    );

    void sendGameEvent(final String name,
                       final String data,
                       final String feed);

    void changeV2StatePauseTime(long newTime);
}
