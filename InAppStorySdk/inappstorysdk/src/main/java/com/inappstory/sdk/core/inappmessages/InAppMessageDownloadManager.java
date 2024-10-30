package com.inappstory.sdk.core.inappmessages;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.dataholders.models.IReaderContent;
import com.inappstory.sdk.core.network.content.usecase.InAppMessageByIdUseCase;
import com.inappstory.sdk.core.network.content.usecase.InAppMessagesUseCase;
import com.inappstory.sdk.core.ui.screens.IReaderContentPageViewModel;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.cache.SlideTaskKey;
import com.inappstory.sdk.stories.cache.SlidesDownloader;

import java.io.IOException;
import java.util.List;

public class InAppMessageDownloadManager {
    private final IASCore core;

    private final SlidesDownloader slidesDownloader;

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
                        public void success(IReaderContent content) {

                        }

                        @Override
                        public void error() {
                            core.callbacksAPI().useCallback(
                                    IASCallbackType.IN_APP_MESSAGE_LOAD,
                                    new UseIASCallback<InAppMessageLoadCallback>() {
                                        @Override
                                        public void use(@NonNull InAppMessageLoadCallback callback) {
                                            callback.loadError(
                                                    Integer.toString(inAppMessageId)
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
        }
    }

    private void addSlides(@NonNull IReaderContent readerContent) {
        slidesDownloader.addStorySlides(
                new ContentIdAndType(readerContent.id(),
                        ContentType.IN_APP_MESSAGE
                ),
                readerContent,
                3
        );
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
    }
}
