package com.inappstory.sdk.network;

import com.inappstory.sdk.stories.api.models.StatisticSendObject;

public class DumbApiInterface implements ApiInterface {
    @Override
    public Request getUgcStories(String f, String fields, String expand) {
        return new DumbRequest();
    }

    @Override
    public Request getUgcStoryById(String id, Integer srcList, String expand) {
        return new DumbRequest();
    }

    @Override
    public Request getStories(String test, Integer favorite, String tags, String fields) {
        return new DumbRequest();
    }

    @Override
    public Request getFeed(String feed, String test, Integer favorite,
                           String tags, String fields) {
        return new DumbRequest();
    }

    @Override
    public Request getOnboardingFeed(String feed, Integer limit, String tags) {
        return new DumbRequest();
    }

    @Override
    public Request onboardingStories(String tags) {
        return new DumbRequest();
    }

    @Override
    public Request getStoryById(String id, Integer srcList, String expand) {
        return new DumbRequest();
    }

    @Override
    public Request sendBaseStat(String eventName, String sessionId, String userId,
                                Long timestamp, Integer storyId, String whence,
                                String cause, Integer slideIndex,
                                Integer slideTotal, Long durationMs, Long spendMs) {
        return new DumbRequest();
    }

    @Override
    public Request sendException(String session, Long timestamp,
                                 String message,
                                 String file, Integer line, String trace) {
        return new DumbRequest();
    }

    @Override
    public Request sendStat(String eventName, String sessionId, String userId,
                            Long timestamp, String feedId, String storyId,
                            String whence, String cause, Integer slideIndex,
                            Integer slideTotal, Long durationMs,
                            String widgetId, String widgetLabel,
                            String widgetValue, Integer widgetAnswer,
                            String widgetAnswerLabel, Integer widgetAnswerScore,
                            Integer layoutIndex, String t, Integer type) {
        return new DumbRequest();
    }

    @Override
    public Request sendStoryData(String id, String data, String sessionId) {
        return new DumbRequest();
    }

    @Override
    public Request storyLike(String id, Integer value) {
        return new DumbRequest();
    }

    @Override
    public Request storyFavorite(String id, Integer value) {
        return new DumbRequest();
    }

    @Override
    public Request removeAllFavorites() {
        return new DumbRequest();
    }

    @Override
    public Request share(String id, String expand) {
        return new DumbRequest();
    }

    @Override
    public Request sessionOpen(String expand, String features, String platform,
                               String deviceId, String model, String manufacturer,
                               String brand, String screenWidth, String screenHeight,
                               String screenDpi, String osVersion, String osSdkVersion,
                               String appPackageId, String appVersion, String appBuild,
                               String userId) {
        return new DumbRequest();
    }

    @Override
    public Request sessionUpdate(StatisticSendObject request) {
        return new DumbRequest();
    }

    @Override
    public Request sessionClose(StatisticSendObject request) {
        return new DumbRequest();
    }
}
