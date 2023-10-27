package com.inappstory.sdk.stories.uidomain.list.utils;

import com.inappstory.sdk.core.repository.stories.dto.PreviewStoryDTO;

import java.util.List;

public interface GetStoriesList {
   void onSuccess(List<PreviewStoryDTO> stories);

   void onError();
}
