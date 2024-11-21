package com.inappstory.sdk.network;


import com.inappstory.sdk.network.annotations.api.Body;
import com.inappstory.sdk.network.annotations.api.DELETE;
import com.inappstory.sdk.network.annotations.api.ExcludeHeaders;
import com.inappstory.sdk.network.annotations.api.Field;
import com.inappstory.sdk.network.annotations.api.FormUrlEncoded;
import com.inappstory.sdk.network.annotations.api.GET;
import com.inappstory.sdk.network.annotations.api.POST;
import com.inappstory.sdk.network.annotations.api.PUT;
import com.inappstory.sdk.network.annotations.api.Path;
import com.inappstory.sdk.network.annotations.api.Query;
import com.inappstory.sdk.network.annotations.api.QueryObject;
import com.inappstory.sdk.network.annotations.api.ReplaceHeader;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.utils.headers.HeadersKeys;
import com.inappstory.sdk.stories.api.models.GameLaunchConfigObject;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;

/**
 * InAppStory API. Contains all request
 */

public interface ApiInterface {

    @GET("v2/inappmessaging")
    Request getInAppMessages(
            @Query("srcList") Integer srcList,
            @Query("fields") String fields,
            @Query("expand") String expand
    );

    @GET("v2/inappmessaging/message/{id}")
    Request getInAppMessage(
            @Path("id") String id,
            @Query("srcList") Integer srcList,
            @Query("fields") String fields,
            @Query("expand") String expand
    );


    @GET("v2/inappmessaging/message/{id}/event/{event_name}")
    Request sendInAppMessageStat(
            @Path("id") String id,
            @Path("event_name") String eventName,
            @Field("ei") String eventId,
            @Field("ii") String iterationId,
            @Field("si") Integer slideIndex,
            @Field("st") Integer slideTotal,
            @Field("d") Long durationMs,
            @Field("wi") String widgetId,
            @Field("wl") String widgetLabel,
            @Field("wv") String widgetValue,
            @Field("wa") Integer widgetAnswer,
            @Field("wal") String widgetAnswerLabel,
            @Field("was") Integer widgetAnswerScore
    );

    @GET("v2/ugc/feed")
    Request getUgcStories(
            @QueryObject("f") String f,
            @Query("fields") String fields,
            @Query("expand") String expand);

    @GET("v2/ugc/story/{id}")
    Request getUgcStoryById(
            @Path("id") String id,
            @Query("src_list") Integer srcList,
            @Query("expand") String expand
    );

    @POST("v2/game/{id}/launch")
    Request getGameByInstanceId(
            @Path("id") String id,
            @Body GameLaunchConfigObject configObject
    );


    @FormUrlEncoded
    @PUT("v2/game/{id}/instance-user-data")
    Request sendGameData(
            @Path("id") String id,
            @Field("data") String data);

    @GET("v2/story")
    Request getStories(
            @Query("test") String test,
            @Query("favorite") Integer favorite,
            @Query("tags") String tags,
            @Query("fields") String fields,
            @Query("expand") String expand);


    @GET("v2/feed/{feed}")
    Request getFeed(
            @Path("feed") String feed,
            @Query("test") String test,
            @Query("favorite") Integer favorite,
            @Query("tags") String tags,
            @Query("fields") String fields,
            @Query("expand") String expand
    );


    @GET("v2/feed/{feed}/onboarding")
    Request getOnboardingFeed(
            @Path("feed") String feed,
            @Query("test") String test,
            @Query("limit") Integer limit,
            @Query("tags") String tags
    );


    @GET("v2/story/{id}")
    Request getStoryById(
            @Path("id") String id,
            @Query("test") String test,
            @Query("once") Integer once,
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

    @FormUrlEncoded
    @POST("exception")
    @ExcludeHeaders({
            HeadersKeys.ACCEPT,
            HeadersKeys.ACCEPT_LANGUAGE,
            HeadersKeys.DEVICE_ID,
            HeadersKeys.APP_PACKAGE_ID,
            HeadersKeys.AUTHORIZATION
    })
    Request sendException(
            @Query("s") String session,
            @Query("ts") Long timestamp,
            @Field("m") String message,
            @Field("f") String file,
            @Field("l") Integer line,
            @Field("t") String trace);

    @FormUrlEncoded
    @POST("v2/game/{id}/logger")
    Request sendGameLogMessage(
            @Path("id") String gameInstanceId,
            @Field("type") String type,
            @Field("launchTryNumber") int launchTryNumber,
            @Field("timestamp") Long timestamp,
            @Field("message") String message,
            @Field("stacktrace") String stacktrace,
            @Field("logSession") String logSession,
            @Field("gameLaunched") boolean gameLaunched
    );




    @GET("stat/{event_name}")
    @ExcludeHeaders({
            HeadersKeys.ACCEPT,
            HeadersKeys.ACCEPT_LANGUAGE,
            HeadersKeys.DEVICE_ID,
            HeadersKeys.APP_PACKAGE_ID,
            HeadersKeys.AUTHORIZATION
    })
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


    @FormUrlEncoded
    @PUT("v2/story-data/{id}")
    Request sendStoryData(
            @Path("id") String id,
            @Field("data") String data,
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

    @GET("v2/ugc/editor-config")
    Request getUgcEditor();


    @GET("v2/game/preload")
    @ExcludeHeaders({
            HeadersKeys.AUTH_SESSION_ID
    })
    Request getPreloadGames(
            @Query("hasFeatureWebp") boolean hasFeatureWebp
    );

    @FormUrlEncoded
    @POST("v2/session/open")
    @ExcludeHeaders({
            HeadersKeys.AUTH_SESSION_ID
    })
    Request sessionOpen(
            @Query("fields") String fields,
            @Query("expand") String expand,
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
            @Field("user_id") String userId
    );

    @POST("v2/session/update")
    Request sessionUpdate(
            @Body StatisticSendObject request);

    @POST("v2/session/close")
    Request sessionClose(
            @Body StatisticSendObject request,
            @ReplaceHeader(HeadersKeys.USER_ID) String xUserId,
            @ReplaceHeader(HeadersKeys.ACCEPT_LANGUAGE) String lang
    );

}
