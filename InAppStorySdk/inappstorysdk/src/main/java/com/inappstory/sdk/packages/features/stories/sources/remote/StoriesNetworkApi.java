package com.inappstory.sdk.packages.features.stories.sources.remote;

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
import com.inappstory.sdk.network.models.Request;

public interface StoriesNetworkApi {
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
            @Query("fields") String fields);


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
            @Query("limit") Integer limit,
            @Query("tags") String tags
    );

    @GET("v2/story-onboarding")
    Request onboardingStories(
            @Query("tags") String tags);

    @GET("v2/story/{id}")
    Request getStoryById(
            @Path("id") String id,
            @Query("once") Integer once,
            @Query("src_list") Integer srcList,
            @Query("expand") String expand
    );

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
}
