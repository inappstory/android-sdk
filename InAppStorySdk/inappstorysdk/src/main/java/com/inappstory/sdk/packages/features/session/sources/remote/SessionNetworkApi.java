package com.inappstory.sdk.packages.features.session.sources.remote;

import com.inappstory.sdk.network.annotations.api.Body;
import com.inappstory.sdk.network.annotations.api.ExcludeHeaders;
import com.inappstory.sdk.network.annotations.api.Field;
import com.inappstory.sdk.network.annotations.api.FormUrlEncoded;
import com.inappstory.sdk.network.annotations.api.POST;
import com.inappstory.sdk.network.annotations.api.Query;
import com.inappstory.sdk.network.annotations.api.ReplaceHeader;
import com.inappstory.sdk.network.models.Request;
import com.inappstory.sdk.network.utils.headers.HeadersKeys;
import com.inappstory.sdk.stories.api.models.StatisticSendObject;

public interface SessionNetworkApi {
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
            @Body StatisticSendObject request
    );

    @POST("v2/session/close")
    Request sessionClose(
            @Body StatisticSendObject request,
            @ReplaceHeader(HeadersKeys.USER_ID) String xUserId,
            @ReplaceHeader(HeadersKeys.ACCEPT_LANGUAGE) String lang
    );
}
