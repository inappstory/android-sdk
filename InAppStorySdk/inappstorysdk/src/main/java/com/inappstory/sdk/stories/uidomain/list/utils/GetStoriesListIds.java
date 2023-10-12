package com.inappstory.sdk.stories.uidomain.list.utils;

import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public interface GetStoriesListIds {
   void onSuccess(List<StoriesAdapterStoryData> storiesIds);

   void onError();
}
