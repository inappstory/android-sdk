package io.casestory.sdk.network;


import java.util.List;

import io.casestory.sdk.stories.api.models.StatisticSendObject;

/**
 * CaseStory API. Contains all request
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
                                        @Query("tags") List<String> tags,
                                        @Query("token") String token);

    @GET("v2/story/{id}")
    Request getStoryById(@Path("id") String id,
                             @Query("session_id") String sessionId,
                             @Query("src_list") Integer srcList,
                             @Query("token") String token,
                             @Query("expand") String expand
    );

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
