package com.inappstory.sdk.core.api;

import java.util.List;

public interface IASStatisticV1 extends Disabled {
    void refreshTimer();
    List<List<Object>> extractCurrentStatistic();
    void previewStatisticEvent(List<Integer> vals);
    void sendStatistic();
    void increaseEventCount();
    void closeStatisticEvent(final Integer time, boolean clear);
    void closeStatisticEvent();
    void addStatisticBlock(int storyId, int index);
    void addLinkOpenStatistic(int storyId, int slideIndex);
    void addDeeplinkClickStatistic(int id);
    void addGameClickStatistic(int id);
    void restartSchedule();
    void refreshCurrentState();
    void clearCurrentState();
}
