package com.inappstory.sdk.core.api;

import java.util.List;

public interface IASStatisticStoriesV2 extends StatDisabled {

    void cleanTasks();

    void pauseStoryEvent(boolean withBg);

    void resumeStoryEvent(boolean withBg);

    void cleanFakeEvents();

    void sendViewStory(final int storyId, final String whence, final String feedId);

    void sendGoodsOpen(final int storyId,
                       final int slideIndex,
                       final String widgetId,
                       final String feedId);

    void sendGoodsClick(final int i, final int si,
                        final String wi, final String sku,
                        final String feedId);

    void sendViewStory(List<Integer> ids, final String w,
                       final String feedId);

    void sendOpenStory(final int i, final String w,
                       final String feedId);

    void sendCloseStory(final int i,
                        final String c,
                        final Integer si,
                        final Integer st,
                        final String feedId);

    void sendCurrentState();

    void createCurrentState(final int stId,
                            final int ind,
                            final String feedId);

    void addFakeEvents(final int i,
                       final Integer si,
                       final Integer st,
                       final String feedId);

    void sendDeeplinkStory(final int i,
                           String link,
                           final String feedId);

    void sendClickLink(int storyId);

    void sendLikeStory(final int i,
                       final int si,
                       final String feedId);

    void sendDislikeStory(final int i,
                          final int si,
                          final String feedId);
    void sendFavoriteStory(final int i,
                           final int si,
                           final String feedId);

    void sendViewSlide(final int i,
                       final int si,
                       final Long t,
                       final String feedId);

    void sendShareStory(final int i,
                        final int si,
                        int mode,
                        final String feedId);

    void sendStoryWidgetEvent(final String name,
                              final String data,
                              final String feedId,
                              boolean forceSend);

    void sendGameEvent(final String name,
                       final String data,
                       final String feedId);

    void changeV2StatePauseTime(long newTime);
}
