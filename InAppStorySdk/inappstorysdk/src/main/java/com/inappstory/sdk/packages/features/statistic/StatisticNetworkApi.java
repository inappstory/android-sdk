package com.inappstory.sdk.packages.features.statistic;

import com.inappstory.sdk.network.annotations.api.ExcludeHeaders;
import com.inappstory.sdk.network.annotations.api.Field;
import com.inappstory.sdk.network.annotations.api.FormUrlEncoded;
import com.inappstory.sdk.network.annotations.api.GET;
import com.inappstory.sdk.network.annotations.api.POST;
import com.inappstory.sdk.network.annotations.api.Path;
import com.inappstory.sdk.network.annotations.api.Query;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.utils.headers.HeadersKeys;

public interface StatisticNetworkApi {
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

}
