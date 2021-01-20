package com.inappstory.sdk.network;


import com.inappstory.sdk.stories.api.models.StatisticSendObject;

/**
 * InAppStory API. Contains all request
 */

public interface ApiInterface {

    @GET("v2/story")
    Request getStories(@Query("session_id") String sessionId,
                       @Query("tags") String tags,
                       @Query("test") String test,
                       @Query("token") String token);

    @GET("v2/story")
    Request getStories(@Query("session_id") String sessionId,
                       @Query("test") String test,
                       @Query("favorite") Integer favorite,
                       @Query("tags") String tags,
                       @Query("fields") String fields,
                       @Query("token") String token);

    @GET("v2/story")
    Request getStories(@Query("session_id") String sessionId,
                       @Query("tags") String tags,
                       @Query("favorite") Integer favorite,
                       @Query("token") String token);

    @GET("v2/story-onboarding")
    Request onboardingStories(@Query("session_id") String sessionId,
                              @Query("tags") String tags,
                              @Query("token") String token);

    @GET("v2/story/{id}")
    Request getStoryById(@Path("id") String id,
                         @Query("session_id") String sessionId,
                         @Query("src_list") Integer srcList,
                         @Query("token") String token,
                         @Query("expand") String expand
    );

    @GET("stat/{event_name}")
    Request sendBaseStat(@Path("event_name") String eventName,
                         @Query("session_id") String sessionId,
                         @Query("user_id") String userId,
                         @Query("timestamp") Long timestamp,
                         @Query("story_id") Integer storyId,
                         @Query("whence") String whence,
                         @Query("cause") String cause,
                         @Query("slide_index") Integer slideIndex,
                         @Query("slide_total") Integer slideTotal,
                         @Query("duration_ms") Long durationMs,
                         @Query("spend_ms") Long spendMs);


    @GET("stat/{event_name}")
    Request sendStat(@Path("event_name") String eventName,
                           @Query("session_id") String sessionId,
                           @Query("user_id") String userId,
                           @Query("timestamp") Long timestamp,
                           @Query("story_id") String storyId,
                           @Query("whence") String whence,
                           @Query("cause") String cause,
                           @Query("slide_index") Integer slideIndex,
                           @Query("slide_total") Integer slideTotal,
                           @Query("duration_ms") Long durationMs,
                           @Query("widget_id") String widgetId,
                           @Query("widget_label") String widgetLabel,
                           @Query("widget_value") String widgetValue,
                           @Query("widget_answer") Integer widgetAnswer,
                           @Query("widget_answer_label") String widgetAnswerLabel,
                           @Query("widget_answer_score") Integer widgetAnswerScore,
                           @Query("layout_index") Integer layoutIndex);


    @FormUrlEncoded
    @PUT("v2/story-data/{id}")
    Request sendStoryData(@Path("id") String id,
                          @Field("data") String data,
                          @Query("session_id") String sessionId);

    @POST("v2/story-like/{id}")
    Request storyLike(@Path("id") String id,
                      @Query("session_id") String sessionId,
                      @Query("token") String token,
                      @Query("value") Integer value);

    @POST("v2/story-favorite/{id}")
    Request storyFavorite(@Path("id") String id,
                          @Query("session_id") String sessionId,
                          @Query("token") String token,
                          @Query("value") Integer value);


    @GET("v2/story-share/{id}")
    Request share(@Path("id") String id,
                  @Query("session_id") String sessionId,
                  @Query("token") String token,
                  @Query("expand") String expand
    );

    @FormUrlEncoded
    @POST("v2/session/open")
    Request statisticsOpen(@Query("expand") String expand,
                           @Field("tags") String tags,
                           @Field("features") String features,
                           @Field("platform") String platform,
                           @Field("device_id") String deviceId,
                           @Field("model") String model,
                           @Field("manufacturer") String manufacturer,
                           @Field("brand") String brand,
                           @Field("screen_width") String screenWidth,
                           @Field("screen_height") String screenHeight,
                           @Field("screen_dpi") String screenDpi,
                           @Field("os_version") String osVersion,
                           @Field("os_sdk_version") String osSdkVersion,
                           @Field("app_package_id") String appPackageId,
                           @Field("app_version") String appVersion,
                           @Field("app_build") String appBuild,
                           @Field("user_id") String userId);

    @POST("v2/session/update")
    Request statisticsUpdate(@Body StatisticSendObject request);

    @POST("v2/session/close")
    Request statisticsClose(@Body StatisticSendObject request);

}
