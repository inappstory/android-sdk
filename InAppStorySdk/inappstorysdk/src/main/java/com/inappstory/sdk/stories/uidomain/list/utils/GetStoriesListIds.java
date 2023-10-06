package com.inappstory.sdk.stories.uidomain.list.utils;

import java.util.List;

public interface GetStoriesListIds {
   void onSuccess(List<Integer> storiesIds);

   void onError();
}
