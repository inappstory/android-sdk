package com.inappstory.sdk.stories.ui.list;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.data.IFavoriteItem;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.usecases.IGetStoryCoverCallback;
import com.inappstory.sdk.stories.cache.usecases.StoryCoverUseCase;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.utils.RunnableCallback;

import java.util.ArrayList;
import java.util.List;

public class StoryFavoriteListItem extends BaseStoryListItem {

    public StoryFavoriteListItem(@NonNull View itemView, ViewGroup parent, AppearanceManager manager) {
        super(itemView, parent, manager, true, false);
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        vg.addView(getDefaultFavoriteCell());
    }

    protected View getDefaultFavoriteCell() {
        View v = null;
        if (getFavoriteListItem != null && getFavoriteListItem.getFavoriteItem() != null) {
            v = getFavoriteListItem.getFavoriteItem();
        }
        return v;
    }

    private void loadFavoriteImages(final LoadFavoriteImagesCallback callback, final int count) {
        final List<String> downloadImages = new ArrayList<>();
        final int[] i = {0};
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        final List<IFavoriteItem> favoriteImages = inAppStoryManager
                .iasCore()
                .contentHolder()
                .favoriteItems().getByType(ContentType.STORY);
        if (count == 0 || favoriteImages.isEmpty()) {
            return;
        }
        RunnableCallback runnableCallback = new RunnableCallback() {
            @Override
            public void run(String path) {
                downloadImages.add(path);
                i[0]++;
                if (i[0] >= count)
                    callback.onLoad(downloadImages);
                else {
                    if (favoriteImages.size() <= i[0]) {
                        downloadFileAndSendToInterface("", this);
                    } else {
                        downloadFileAndSendToInterface(favoriteImages.get(i[0]).imageUrl(), this);
                    }
                }
            }

            @Override
            public void error() {
                downloadImages.add(null);
                i[0]++;
                if (i[0] >= count)
                    callback.onLoad(downloadImages);
                else {
                    if (favoriteImages.size() <= i[0]) {
                        downloadFileAndSendToInterface("", this);
                    } else {
                        downloadFileAndSendToInterface(favoriteImages.get(i[0]).imageUrl(), this);
                    }
                }
            }
        };
        downloadFileAndSendToInterface(favoriteImages.get(0).imageUrl(), runnableCallback);
    }

    interface LoadFavoriteImagesCallback {
        void onLoad(List<String> downloadImages);
    }

    @Override
    public void bind(
            Integer id,
            String titleText,
            Integer titleColor,
            String imageUrl,
            Integer backgroundColor,
            boolean isOpened,
            boolean hasAudio,
            String videoUrl,
            StoryData storyData,
            ClickCallback callback
    ) {

    }

    public void bindFavorite() {

        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        List<IFavoriteItem> favoriteImages = inAppStoryManager
                .iasCore()
                .contentHolder()
                .favoriteItems().getByType(ContentType.STORY);
        if (getFavoriteListItem != null
                && getFavoriteListItem.getFavoriteItem() != null) {
            int count = favoriteImages.size();
            final List<Integer> backgroundColors = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                backgroundColors.add(favoriteImages.get(j).backgroundColor());
            }
            if (viewCanBeUsed(itemView, getParent())) {
                getFavoriteListItem.bindFavoriteItem(itemView, backgroundColors, count);
            }
            loadFavoriteImages(new LoadFavoriteImagesCallback() {
                @Override
                public void onLoad(final List<String> downloadImages) {
                    itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (viewCanBeUsed(itemView, getParent())) {
                                if (getFavoriteListItem != null
                                        && getFavoriteListItem.getFavoriteItem() != null) {
                                    getFavoriteListItem.setImages(
                                            itemView,
                                            downloadImages,
                                            backgroundColors,
                                            downloadImages.size()
                                    );
                                }
                            }
                        }
                    });

                }
            }, count);
        }
    }

    @Override
    public void bindUGC() {

    }

    private void downloadFileAndSendToInterface(final String url, final RunnableCallback callback) {
        if (url == null || url.isEmpty()) {
            callback.error();
            return;
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                new StoryCoverUseCase(
                        core,
                        url,
                        new IGetStoryCoverCallback() {
                            @Override
                            public void success(final String file) {
                                callback.run(file);
                            }

                            @Override
                            public void error() {
                                callback.error();
                            }
                        }
                ).getFile();
            }

            @Override
            public void error() {
                callback.error();
            }
        });


    }

    public Integer backgroundColor;
    public ClickCallback callback;
}
