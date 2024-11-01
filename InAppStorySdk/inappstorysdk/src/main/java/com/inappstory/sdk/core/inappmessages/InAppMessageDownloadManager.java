package com.inappstory.sdk.core.inappmessages;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.network.content.usecase.InAppMessageByIdUseCase;
import com.inappstory.sdk.core.ui.screens.IReaderContentPageViewModel;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.cache.SlideTaskKey;
import com.inappstory.sdk.stories.cache.SlidesDownloader;
import com.inappstory.sdk.utils.ISessionHolder;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InAppMessageDownloadManager {
    private final IASCore core;

    private final SlidesDownloader slidesDownloader;
    private final Set<Integer> loadedInAppMessages = new HashSet<>();

    public InAppMessageDownloadManager(IASCore core) {
        this.core = core;
        this.slidesDownloader = new SlidesDownloader(
                core,
                null
        );
        this.slidesDownloader.init();
    }

    public void addInAppMessageTask(final int inAppMessageId, ContentType type) {
        IReaderContent readerContent =
                core.contentHolder().readerContent().getByIdAndType(inAppMessageId, type);
        if (readerContent != null) {
            addSlides(readerContent);
        } else {
            new InAppMessageByIdUseCase(core, inAppMessageId).get(
                    new InAppMessageByIdCallback() {
                        @Override
                        public void success(IReaderContent readerContent) {
                            addSlides(readerContent);
                        }

                        @Override
                        public void error() {
                            core.callbacksAPI().useCallback(
                                    IASCallbackType.IN_APP_MESSAGE_LOAD,
                                    new UseIASCallback<InAppMessageLoadCallback>() {
                                        @Override
                                        public void use(@NonNull InAppMessageLoadCallback callback) {
                                            callback.loadError(
                                                    inAppMessageId
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
        }
    }

    public boolean allSlidesLoaded(IReaderContent readerContent) {
        return slidesDownloader.allSlidesLoaded(readerContent, ContentType.IN_APP_MESSAGE);
    }

    private void addSlides(@NonNull final IReaderContent readerContent) {
        core.contentLoader().inAppMessageDownloadManager().addSubscriber(
                new IReaderContentPageViewModel() {
                    @Override
                    public ContentIdAndType contentIdAndType() {
                        return new ContentIdAndType(
                                readerContent.id(),
                                ContentType.IN_APP_MESSAGE
                        );
                    }

                    @Override
                    public void contentLoadError() {
                        core.callbacksAPI().useCallback(
                                IASCallbackType.IN_APP_MESSAGE_LOAD,
                                new UseIASCallback<InAppMessageLoadCallback>() {
                                    @Override
                                    public void use(@NonNull InAppMessageLoadCallback callback) {
                                        callback.loadError(
                                                readerContent.id()
                                        );
                                    }
                                }
                        );
                    }

                    @Override
                    public void slideLoadError(int index) {
                        core.callbacksAPI().useCallback(
                                IASCallbackType.IN_APP_MESSAGE_LOAD,
                                new UseIASCallback<InAppMessageLoadCallback>() {
                                    @Override
                                    public void use(@NonNull InAppMessageLoadCallback callback) {
                                        callback.loadError(
                                                readerContent.id()
                                        );
                                    }
                                }
                        );
                    }

                    @Override
                    public void contentLoadSuccess(IReaderContent content) {

                    }

                    @Override
                    public void slideLoadSuccess(int index) {
                        if (core.contentLoader().inAppMessageDownloadManager()
                                .allSlidesLoaded(readerContent)) {

                            ISessionHolder sessionHolder = core.sessionManager().getSession();
                            if (sessionHolder.checkIfSessionAssetsIsReadySync()) {
                                contentIsLoaded(readerContent);
                            } else {
                                sessionHolder.addSessionAssetsIsReadyCallback(new SessionAssetsIsReadyCallback() {
                                    @Override
                                    public void isReady() {
                                        contentIsLoaded(readerContent);
                                    }
                                });
                            }
                        }
                    }
                }
        );


        slidesDownloader.addStorySlides(
                new ContentIdAndType(readerContent.id(),
                        ContentType.IN_APP_MESSAGE
                ),
                readerContent,
                3
        );
    }

    private void contentIsLoaded(final IReaderContent readerContent) {
        loadedInAppMessages.add(readerContent.id());
        core.callbacksAPI().useCallback(
                IASCallbackType.IN_APP_MESSAGE_LOAD,
                new UseIASCallback<InAppMessageLoadCallback>() {
                    @Override
                    public void use(@NonNull InAppMessageLoadCallback callback) {
                        callback.loaded(
                                readerContent.id()
                        );
                        if (allContentIsLoaded()) {
                            callback.allLoaded();
                        }
                    }
                }
        );

    }

    private boolean allContentIsLoaded() {
        List<IReaderContent> readerContentList =
                core.contentHolder().readerContent().getByType(ContentType.IN_APP_MESSAGE);
        for (IReaderContent readerContent : readerContentList) {
            if (!loadedInAppMessages.contains(readerContent.id())) return false;
        }
        return true;
    }

    public void addSubscriber(IReaderContentPageViewModel pageViewModel) {
        slidesDownloader.addSubscriber(pageViewModel);
    }

    public void removeSubscriber(IReaderContentPageViewModel pageViewModel) {
        slidesDownloader.removeSubscriber(pageViewModel);
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
        core.contentHolder().readerContent().clearByType(ContentType.IN_APP_MESSAGE);
        loadedInAppMessages.clear();
    }
}
