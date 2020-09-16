package io.casestory.sdk.stories.api.networkclient;


import java.util.List;

import io.casestory.sdk.stories.api.models.ShareObject;
import io.casestory.sdk.stories.api.models.StatisticResponse;
import io.casestory.sdk.stories.api.models.StatisticSendObject;
import io.casestory.sdk.stories.api.models.Story;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Kiozk API. Contains all request for applications
 */

public interface ApiInterface {

    @GET("v1/narrative")
    Call<List<Story>> getStories(@Query("session_id") String sessionId,
                                 @Query("tags") String tags,
                                 @Query("test") String test,
                                 @Query("token") String token);

    @GET("v1/narrative")
    Call<List<Story>> getStories(@Query("session_id") String sessionId,
                                 @Query("test") String test,
                                 @Query("favorite") Integer favorite,
                                 @Query("tags") String tags,
                                 @Query("fields") String fields,
                                 @Query("token") String token);

    @GET("v1/narrative")
    Call<List<Story>> getStories(@Query("session_id") String sessionId,
                                 @Query("tags") String tags,
                                 @Query("favorite") Integer favorite,
                                 @Query("token") String token);

    @GET("v1/narrative-popup")
    Call<List<Story>> popupStories(@Query("session_id") String sessionId,
                                   @Query("tags") String tags,
                                   @Query("token") String token);

    @GET("v1/narrative/{id}")
    Call<Story> getStoryById(@Path("id") String id,
                             @Query("session_id") String sessionId,
                             @Query("token") String token,
                             @Query("expand") String expand
    );

    @FormUrlEncoded
    @PUT("v1/narrative-data/{id}")
    Call<ResponseBody> sendStoryData(@Path("id") String id,
                                     @Field("data") String data,
                                     @Query("session_id") String sessionId);

    @POST("v1/narrative-like/{id}")
    Call<ResponseBody> storyLike(@Path("id") String id,
                                 @Query("session_id") String sessionId,
                                 @Query("token") String token,
                                 @Query("value") Integer value);

    @POST("v1/narrative-favorite/{id}")
    Call<ResponseBody> storyFavorite(@Path("id") String id,
                                     @Query("session_id") String sessionId,
                                     @Query("token") String token,
                                     @Query("value") Integer value);



    @GET("v1/narrative-share/{id}")
    Call<ShareObject> share(@Path("id") String id,
                            @Query("session_id") String sessionId,
                            @Query("token") String token,
                            @Query("expand") String expand
    );

    @FormUrlEncoded
    @POST("v1/session/open")
    Call<StatisticResponse> statisticsOpen(@Query("expand") String expand,
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

    @POST("v1/session/update")
    Call<StatisticResponse> statisticsUpdate(@Body StatisticSendObject request);

    @POST("v1/session/close")
    Call<StatisticResponse> statisticsClose(@Body StatisticSendObject request);


   /* @GET("v1/article/{id}")
    Call<Article> article(@Path("id") String id,
                          @Query("session_id") String sessionId,
                          @Query("expand") String expand
    );


    @GET("v1/issue-article/{id}")
    Call<Article> issueArticle(@Path("id") String id,
                               @Query("session_id") String sessionId,
                               @Query("expand") String expand
    );

*/
}
