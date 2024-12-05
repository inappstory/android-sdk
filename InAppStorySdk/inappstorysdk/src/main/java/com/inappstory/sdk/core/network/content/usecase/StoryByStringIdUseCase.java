package com.inappstory.sdk.core.network.content.usecase;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.network.ApiSettings;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

import java.lang.reflect.Type;

public class StoryByStringIdUseCase {
    private final IASCore core;

    public StoryByStringIdUseCase(IASCore core) {
        this.core = core;
    }

    private final String EXPAND_STRING = "slides,layout";


    public void get(
            final String id,
            final GetStoryByIdCallback storyByIdCallback,
            final boolean showOnce,
            final SourceType readerSource
    ) {
        core.sessionManager().useOrOpenSession(
                new OpenSessionCallback() {
                    @Override
                    public void onSuccess(
                            final RequestLocalParameters requestLocalParameters
                    ) {
                        final String storyUID = core.statistic().profiling().addTask("api_story");
                        core.network().enqueue(
                                core.network().getApi().getStoryById(
                                        id,
                                        ApiSettings.getInstance().getTestKey(),
                                        showOnce ? 1 : 0,
                                        1,
                                        EXPAND_STRING,
                                        requestLocalParameters.userId,
                                        requestLocalParameters.sessionId,
                                        requestLocalParameters.locale
                                ),
                                new NetworkCallback<Story>() {
                                    @Override
                                    public void onSuccess(final Story response) {
                                        core.statistic().profiling().setReady(
                                                storyUID
                                        );
                                        core.callbacksAPI().useCallback(
                                                IASCallbackType.SINGLE,
                                                new UseIASCallback<SingleLoadCallback>() {
                                                    @Override
                                                    public void use(@NonNull SingleLoadCallback callback) {
                                                        callback.singleLoadSuccess(
                                                                StoryData.getStoryData(
                                                                        response,
                                                                        null,
                                                                        readerSource,
                                                                        ContentType.STORY
                                                                )
                                                        );
                                                    }
                                                }
                                        );
                                        updateListItem(response);
                                        if (storyByIdCallback != null)
                                            storyByIdCallback.getStory(
                                                    response,
                                                    requestLocalParameters.sessionId
                                            );
                                    }

                                    @Override
                                    public Type getType() {
                                        return Story.class;
                                    }

                                    @Override
                                    public void emptyContent() {
                                        if (storyByIdCallback != null)
                                            storyByIdCallback.loadError(-2);
                                    }

                                    @Override
                                    public void errorDefault(String message) {
                                        core.statistic().profiling().setReady(storyUID);
                                        core.callbacksAPI().useCallback(
                                                IASCallbackType.SINGLE,
                                                new UseIASCallback<SingleLoadCallback>() {
                                                    @Override
                                                    public void use(@NonNull SingleLoadCallback callback) {
                                                        callback.singleLoadError(id, "Can't load story");
                                                    }
                                                }
                                        );
                                        if (storyByIdCallback != null)
                                            storyByIdCallback.loadError(-1);
                                    }
                                },
                                requestLocalParameters
                        );
                    }

                    @Override
                    public void onError() {
                        core.callbacksAPI().useCallback(
                                IASCallbackType.SINGLE,
                                new UseIASCallback<SingleLoadCallback>() {
                                    @Override
                                    public void use(@NonNull SingleLoadCallback callback) {
                                        callback.singleLoadError(id, "Can't open session");
                                    }
                                }
                        );
                        if (storyByIdCallback != null)
                            storyByIdCallback.loadError(-1);
                    }

                }
        );
    }

    private void updateListItem(final Story story) {
        IListItemContent listItemContent = core.contentHolder()
                .listsContent().getByIdAndType(story.id(), ContentType.STORY);
        if (listItemContent == null) {
            listItemContent = story;
            core.contentHolder().listsContent().setByIdAndType(
                    listItemContent,
                    story.id(),
                    ContentType.STORY
            );
        } else {
            listItemContent.setOpened(
                    listItemContent.isOpened() || story.isOpened()
            );
        }
    }
}
