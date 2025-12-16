package com.inappstory.sdk.externalapi.storylist;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.api.IASStoryList;

import java.util.List;

public class IASStoryListExternalAPIImpl implements IASStoryList {
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
                service.getApiSubscribersManager().clearData();
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

    public void openStoryReader(
            final Context context,
            final String uniqueKey,
            final int storyId,
            final AppearanceManager appearanceManager
    ) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getApiSubscribersManager().openStoryReader(
                        context,
                        uniqueKey,
                        storyId,
                        appearanceManager
                );
            }
        });
    }

    public void showFavoriteItem(final String uniqueId) {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getApiSubscribersManager().showFavoriteItem(
                        uniqueId
                );
            }
        });
    }

    public void updateVisiblePreviews(final List<Integer> storyIds, final String uniqueId) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull final IASCore core) {
                InAppStoryService.useInstance(new UseServiceInstanceCallback() {
                    @Override
                    public void use(@NonNull InAppStoryService service) throws Exception {
                        service.getApiSubscribersManager().updateVisiblePreviews(
                                ((IASDataSettingsHolder)core.settingsAPI()).sessionIdOrEmpty(),
                                storyIds,
                                uniqueId
                        );
                    }
                });
            }
        });

    }
}
