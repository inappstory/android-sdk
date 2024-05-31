package com.inappstory.sdk.externalapi.storylist;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;

import java.util.List;

public class IASStoryList {
    public void load(
            final String feed,
            final String uniqueId,
            final boolean hasFavorite,
            final boolean isFavorite,
            final List<String> tags
    ) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                IASStoryListRequestData data = new IASStoryListRequestData(
                        feed,
                        uniqueId,
                        tags,
                        hasFavorite,
                        isFavorite
                );
                service.getApiSubscribersManager().requestsData
                        .put(uniqueId, data);
                service.getApiSubscribersManager().getStoryList(data);
            }
        });
    }

    public void updateVisiblePreviews(final List<Integer> storyIds, final String uniqueId) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getApiSubscribersManager().updateVisiblePreviews(
                        service.getSession().getSessionId(),
                        storyIds,
                        uniqueId
                );
            }
        });
    }
}
