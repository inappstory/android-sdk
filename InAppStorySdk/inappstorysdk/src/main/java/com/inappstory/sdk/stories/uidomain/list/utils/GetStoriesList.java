package com.inappstory.sdk.stories.uidomain.list.utils;

import com.inappstory.sdk.stories.uidomain.list.StoriesAdapterStoryData;

import java.util.List;

public interface GetStoriesList {
   void onSuccess(List<StoriesAdapterStoryData> stories);

   void onError();
}
