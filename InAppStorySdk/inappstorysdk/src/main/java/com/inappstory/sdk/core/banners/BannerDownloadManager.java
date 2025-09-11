package com.inappstory.sdk.core.banners;

import androidx.annotation.NonNull;

import com.inappstory.sdk.banners.BannerPlacePreloadCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.cache.SlideTaskKey;
import com.inappstory.sdk.stories.cache.SlidesDownloader;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BannerDownloadManager {
    private final IASCore core;

    private final SlidesDownloader slidesDownloader;
    private final Set<Integer> loadedBanners = new HashSet<>();

    public BannerDownloadManager(IASCore core) {
        this.core = core;
        this.slidesDownloader = new SlidesDownloader(
                core,
                null
        );
        this.slidesDownloader.init();
    }

    public void addBannerTask(
            final int bannerId,
            final BannerPlacePreloadCallback callback,
            boolean isFirst
    ) {
        ContentType type = ContentType.BANNER;
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(bannerId, type);
        if (readerContent != null) {
            addSlides(readerContent, callback, isFirst);
        }
    }

    public boolean allSlidesLoaded(IReaderContent readerContent) {
        return slidesDownloader.allSlidesLoaded(readerContent, ContentType.BANNER);
    }

    public boolean checkBundleResources(
            final IReaderSlideViewModel pageViewModel,
            boolean sync
    ) {
        if (sync) return allBundlesLoaded();
        slidesDownloader.checkBundleResources(pageViewModel, 0);
        return true;
    }

    public boolean allBundlesLoaded() {
        return core.assetsHolder().assetsIsDownloaded();
    }

    private void addSlides(
            @NonNull final IReaderContent readerContent,
            final BannerPlacePreloadCallback callback,
            final boolean isFirst
    ) {
        if (allSlidesLoaded(readerContent)) {
            contentIsLoaded(readerContent, callback, isFirst);
        }
        core.contentLoader().bannerDownloadManager().addSubscriber(
                new IReaderSlideViewModel() {
                    @Override
                    public ContentIdAndType contentIdAndType() {
                        return new ContentIdAndType(
                                readerContent.id(),
                                ContentType.BANNER
                        );
                    }

                    @Override
                    public Integer externalSubscriber() {
                        return readerContent.id();
                    }

                    @Override
                    public void renderReady() {

                    }

                    @Override
                    public void contentLoadError() {
                        if (callback != null)
                            callback.bannerContentLoadError(readerContent.id(), isFirst);
                    }

                    @Override
                    public void slideLoadError(int index) {
                        if (callback != null)
                            callback.bannerContentLoadError(readerContent.id(), isFirst);
                    }

                    @Override
                    public void contentLoadSuccess(IReaderContent content) {

                    }

                    @Override
                    public void slideLoadSuccess(int index) {
                        if (core.contentLoader().bannerDownloadManager()
                                .allSlidesLoaded(readerContent)) {
                            contentIsLoaded(readerContent, callback, isFirst);
                        }
                    }

                    @Override
                    public boolean loadContent() {
                        return true;
                    }
                }
        );


        slidesDownloader.addSlides(
                new ContentIdAndType(readerContent.id(),
                        ContentType.BANNER
                ),
                readerContent,
                3,
                true
        );
    }

    public void setMaxPriority(int bannerId, boolean firstPosition) {
        slidesDownloader.setMaxPriority(
                new ContentIdAndType(bannerId,
                        ContentType.BANNER
                ),
                0,
                firstPosition
        );
    }

    private void contentIsLoaded(
            final IReaderContent readerContent,
            BannerPlacePreloadCallback callback,
            boolean isFirst
    ) {
        loadedBanners.add(readerContent.id());
        if (callback != null) {
            callback.bannerContentLoaded(readerContent.id(), isFirst);
        }

    }

    public boolean allContentIsLoaded() {
        List<IReaderContent> readerContentList =
                core.contentHolder().readerContent().getByType(ContentType.BANNER);
        for (IReaderContent readerContent : readerContentList) {
            if (!loadedBanners.contains(readerContent.id())) return false;
        }
        return true;
    }

    public void clearSlidesDownloader() {
        slidesDownloader.cleanTasks();
        slidesDownloader.clearSubscribers();
    }

    public void addSubscriber(IReaderSlideViewModel pageViewModel) {
        slidesDownloader.addSubscriber(pageViewModel);
    }

    public void removeSubscriber(IReaderSlideViewModel pageViewModel) {
        slidesDownloader.removeSubscriber(pageViewModel);
    }

    public void clearSubscribers() {
        slidesDownloader.clearSubscribers();
    }

    public int isSlideLoaded(int id, int index, ContentType type) {
        try {
            return slidesDownloader.isSlideLoaded(
                    new SlideTaskKey(
                            new ContentIdAndType(id, type),
                            index
                    )
            );
        } catch (IOException e) {
            return 0;
        }
    }

    public void clearLocalData() {
        core.contentHolder().readerContent().clearByType(ContentType.BANNER);
        loadedBanners.clear();
    }
}
