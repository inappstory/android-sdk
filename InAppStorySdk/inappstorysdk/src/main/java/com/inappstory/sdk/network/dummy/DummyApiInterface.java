package com.inappstory.sdk.network.dummy;

import com.inappstory.sdk.network.ApiInterface;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.stories.api.models.TargetingBodyObject;
import com.inappstory.sdk.stories.api.models.GameLaunchConfigObject;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;

public class DummyApiInterface implements ApiInterface {

    @Override
    public Request getBannerPlace(String id, Integer srcList, String fields, String expand, TargetingBodyObject filterObject, String xUserId, String xSessionId, String lang) {
        return new DummyRequest();
    }

    @Override
    public Request getInAppMessages(Integer srcList, String ids, String tags, String fields, String expand, String xUserId, String xSessionId, String lang) {
        return new DummyRequest();
    }

    @Override
    public Request getInAppMessagesLimits(String ids) {
        return new DummyRequest();
    }

    @Override
    public Request getInAppMessage(String id, Integer srcList, String fields, String expand, String xUserId, String xSessionId, String lang) {
        return new DummyRequest();
    }

    @Override
    public Request sendInAppMessageStat(String id, String eventName, String eventId, String iterationId, Integer slideIndex, Integer slideTotal, Long durationMs, String widgetId, String widgetLabel, String widgetValue, Integer widgetAnswer, String widgetAnswerLabel, Integer widgetAnswerScore) {
        return new DummyRequest();
    }

    @Override
    public Request sendBannerStat(String id, String eventName, String eventId, String iterationId, Integer slideIndex, Integer slideTotal, Long durationMs, String widgetId, String widgetLabel, String widgetValue, Integer widgetAnswer, String widgetAnswerLabel, Integer widgetAnswerScore) {
        return new DummyRequest();
    }

    @Override
    public Request sendIAMUserData(String id, String data) {
        return new DummyRequest();
    }

    @Override
    public Request sendBannerUserData(String id, String data) {
        return new DummyRequest();
    }

    @Override
    public Request getUgcStories(String f, String fields, String expand) {
        return new DummyRequest();
    }

    @Override
    public Request getUgcStoryById(String id, Integer srcList, String expand) {
        return new DummyRequest();
    }

    @Override
    public Request getGameByInstanceId(String id, GameLaunchConfigObject object) {
        return new DummyRequest();
    }

    @Override
    public Request sendBaseStat(String eventName, String sessionId, String userId,
                                Long timestamp, Integer storyId, String whence,
                                String cause, Integer slideIndex,
                                Integer slideTotal, Long durationMs, Long spendMs) {
        return new DummyRequest();
    }

    @Override
    public Request sendException(String session, Long timestamp,
                                 String message,
                                 String file, Integer line, String trace) {
        return new DummyRequest();
    }

    @Override
    public Request sendGameLogMessage(
            String gameInstanceId,
            String type,
            int launchTryNumber,
            Long timestamp,
            String message,
            String stacktrace,
            String logSession,
            boolean gameLaunched
    ) {
        return new DummyRequest();
    }

    @Override
    public Request sendStat(String eventName, String fullData, String sessionId, String userId,
                            Long timestamp, String feedId, String storyId,
                            String whence, String cause, Integer slideIndex,
                            Integer slideTotal, Long durationMs,
                            String widgetId, String widgetLabel,
                            String widgetValue, Integer widgetAnswer,
                            String widgetAnswerLabel, Integer widgetAnswerScore,
                            Integer layoutIndex, String t, Integer type) {
        return new DummyRequest();
    }

    @Override
    public Request sendGameData(String id, String data) {
        return new DummyRequest();
    }

    @Override
    public Request getStories(String test, Integer favorite, String tags, String fields, String expand, String xUserId, String xSessionId, String lang) {
        return new DummyRequest();
    }

    @Override
    public Request getFeed(String feed, String test, Integer favorite, String tags, String fields, String expand, String xUserId, String xSessionId, String lang) {
        return new DummyRequest();
    }

    @Override
    public Request getOnboardingFeed(String feed, String test, Integer limit, String tags, String expand, String xUserId, String xSessionId, String lang) {
        return new DummyRequest();
    }

    @Override
    public Request getStoryById(String id, String test, Integer once, Integer srcList, String expand, String xUserId, String xSessionId, String lang) {
        return new DummyRequest();
    }

    @Override
    public Request sendStoryData(String id, String data, String sessionId) {
        return new DummyRequest();
    }

    @Override
    public Request storyLike(String id, Integer value) {
        return new DummyRequest();
    }

    @Override
    public Request storyFavorite(String id, Integer value) {
        return new DummyRequest();
    }

    @Override
    public Request removeAllFavorites() {
        return new DummyRequest();
    }

    @Override
    public Request share(String id, String expand) {
        return new DummyRequest();
    }

    @Override
    public Request getUgcEditor() {
        return new DummyRequest();
    }

    @Override
    public Request getPreloadGames(boolean hasFeatureWebp) {
        return new DummyRequest();
    }

    @Override
    public Request sessionOpen(String fields, String expand, String features, String platform,
                               String deviceId, String model, String manufacturer,
                               String brand, String screenWidth, String screenHeight,
                               String screenDpi, String osVersion, String osSdkVersion,
                               String appPackageId, String appVersion, String appBuild,
                               boolean anonymous, String userId, String userSign) {
        return new DummyRequest();
    }

    @Override
    public Request sessionUpdate(StatisticSendObject request) {
        return new DummyRequest();
    }

    @Override
    public Request sessionClose(StatisticSendObject request, String xUserId, String lang) {
        return new DummyRequest();
    }
}
