package com.inappstory.sdk.core.repository.statistic;

import com.inappstory.sdk.core.models.js.StoryIdSlideIndex;

import java.util.List;

public interface IStatisticV1Repository {
    void clear();

    List<List<Object>> getCurrentStatistic();

    void completeCurrentStatisticRecord();

    void onDeeplinkClick(int id);

    void setTypeToTransition(StoryIdSlideIndex storyIdSlideIndex);

    void refreshTimer();

    void forceSend();

    List<Integer> getNonViewedStoryIds(List<Integer> ids);

    void setViewedStoryIds(List<Integer> ids);

    void refreshStatisticProcess();

    void addStatisticEvent(
            StoryIdSlideIndex storyIdSlideIndex
    );
}
