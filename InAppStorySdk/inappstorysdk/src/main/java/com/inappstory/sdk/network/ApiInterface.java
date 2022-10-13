package com.inappstory.sdk.network;


import com.inappstory.sdk.stories.api.models.SendExceptionObject;
import com.inappstory.sdk.stories.api.models.SendStoryDataObject;
import com.inappstory.sdk.stories.api.models.SessionOpenObject;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;

/**
 * InAppStory API. Contains all request
 */

public interface ApiInterface {


    @GET("v2/story")
    Request getStories(
            @Query("test") String test,
            @Query("favorite") Integer favorite,
            @Query("tags") String tags,
            @Query("fields") String fields);

    @GET("v2/feed/{feed}")
    Request getFeed(
            @Path("feed") String feed,
            @Query("test") String test,
            @Query("favorite") Integer favorite,
            @Query("tags") String tags,
            @Query("fields") String fields
    );


    @GET("v2/feed/{feed}/onboarding")
    Request getOnboardingFeed(
            @Path("feed") String feed,
            @Query("tags") String tags
    );

    @GET("v2/story-onboarding")
    Request onboardingStories(
            @Query("tags") String tags);

    @GET("v2/story/{id}")
    Request getStoryById(
            @Path("id") String id,
            @Query("src_list") Integer srcList,
            @Query("expand") String expand
    );

    @GET("stat/{event_name}")
    Request sendBaseStat(
            @Path("event_name") String eventName,
            @Query("s") String sessionId,
            @Query("u") String userId,
            @Query("ts") Long timestamp,
            @Query("i") Integer storyId,
            @Query("w") String whence,
            @Query("c") String cause,
            @Query("si") Integer slideIndex,
            @Query("st") Integer slideTotal,
            @Query("d") Long durationMs,
            @Query("spend_ms") Long spendMs);

    @POST("exception")
    Request sendException(
            @Query("s") String session,
            @Query("ts") Long timestamp,
            @Body SendExceptionObject object);


    @GET("stat/{event_name}")
    Request sendStat(
            @Path("event_name") String eventName,
            @Query("s") String sessionId,
            @Query("u") String userId,
            @Query("ts") Long timestamp,
            @Query("f") String feedId,
            @Query("i") String storyId,
            @Query("w") String whence,
            @Query("c") String cause,
            @Query("si") Integer slideIndex,
            @Query("st") Integer slideTotal,
            @Query("d") Long durationMs,
            @Query("wi") String widgetId,
            @Query("wl") String widgetLabel,
            @Query("wv") String widgetValue,
            @Query("wa") Integer widgetAnswer,
            @Query("wal") String widgetAnswerLabel,
            @Query("was") Integer widgetAnswerScore,
            @Query("li") Integer layoutIndex,
            @Query("t") String t,
            @Query("m") Integer type);

    @PUT("v2/story-data/{id}")
    Request sendStoryData(
            @Path("id") String id,
            @Body SendStoryDataObject data,
            @Query("session_id") String sessionId);

    @POST("v2/story-like/{id}")
    Request storyLike(
            @Path("id") String id,
            @Query("value") Integer value);

    @POST("v2/story-favorite/{id}")
    Request storyFavorite(
            @Path("id") String id,
            @Query("value") Integer value);

    @DELETE("v2/story-favorite")
    Request removeAllFavorites();

    @GET("v2/story-share/{id}")
    Request share(
            @Path("id") String id,
            @Query("expand") String expand
    );

    @POST("v2/session/open")
    Request sessionOpen(
            @Query("expand") String expand,
            @Body SessionOpenObject object);

    @POST("v2/session/update")
    Request sessionUpdate(
            @Body StatisticSendObject request);

    @POST("v2/session/close")
    Request sessionClose(
            @Body StatisticSendObject request);

}
