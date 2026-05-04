package com.inappstory.sdk.refactoring.core.downloader;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.refactoring.core.utils.results.Error;
import com.inappstory.sdk.refactoring.core.utils.results.ResultCallback;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.usecases.GetStoryById;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryDownloadManager {
    StoryDownloadStack currentStoryQueue = new StoryDownloadStack();
    private final IASCore core;
    private final Object downloadLock = new Object();
    private final Object subLock = new Object();
    private ContentIdAndType currentDownloadId = null;
    private final Map<ContentIdAndType, StoryDTO> cachedContent = new HashMap<>();

    public StoryDownloadManager(IASCore core) {
        this.core = core;
        init();
    }

    public void clear() {
        synchronized (downloadLock) {
            currentDownloadId = null;
            currentStoryQueue.clear();
            cachedContent.clear();
        }
    }

    List<IReaderContentDownloaderSubscriber> subscribers = new ArrayList<>();

    public void removeFromCache(ContentIdAndType contentIdAndType) {
        synchronized (downloadLock) {
            cachedContent.remove(contentIdAndType);
        }
    }

    private List<IReaderContentDownloaderSubscriber> getSubscribersByStoryId(ContentIdAndType id) {
        List<IReaderContentDownloaderSubscriber> subscribersById = new ArrayList<>();
        synchronized (subLock) {
            for (IReaderContentDownloaderSubscriber subscriber : subscribers) {
                if (Objects.equals(subscriber.contentIdAndType(), id)) {
                    subscribersById.add(subscriber);
                }
            }
        }
        return subscribersById;
    }

    public void addSubscriber(IReaderContentDownloaderSubscriber subscriber) {
        synchronized (subLock) {
            subscribers.add(subscriber);
        }
    }

    public void removeSubscriber(IReaderContentDownloaderSubscriber subscriber) {
        synchronized (subLock) {
            subscribers.remove(subscriber);
        }
    }

    public void addStories(ContentIdAndType mainId, ContentIdAndType... secondaryIds) {
        List<IReaderContentDownloaderSubscriber> triggerSubs = new ArrayList<>();
        synchronized (downloadLock) {
            for (int i = secondaryIds.length - 1; i >= 0; i--) {
                if (secondaryIds[i] == null) continue;
                if (!Objects.equals(currentDownloadId, secondaryIds[i]))
                    triggerSubs.addAll(addToQueueOrInvokeCallback(secondaryIds[i]));
            }
            if (!Objects.equals(currentDownloadId, mainId))
                triggerSubs.addAll(addToQueueOrInvokeCallback(mainId));
        }
        for (IReaderContentDownloaderSubscriber subscriber : triggerSubs) {
            StoryDTO storyDTO;
            synchronized (downloadLock) {
                storyDTO = cachedContent.get(subscriber.contentIdAndType());
            }
            if (storyDTO != null)
                subscriber.contentLoadSuccess(storyDTO);
        }
    }

    private final Runnable invokeQueueTask = new Runnable() {
        @Override
        public void run() {
            ContentIdAndType id = null;
            synchronized (downloadLock) {
                if (currentDownloadId != null) return;
                currentDownloadId = id = currentStoryQueue.pop();
                if (currentDownloadId == null) {
                    loopedExecutor.freeExecutor();
                    return;
                } else {
                    Log.e("currentDownloadId", "" + currentDownloadId);
                }
            }
            ContentIdAndType finalId = id;
            new GetStoryById(
                    core.sessionRepository(),
                    core.storyRepository(),
                    Integer.toString(id.contentId)
            ).invoke(new ResultCallback<StoryDTO>() {
                @Override
                public void success(StoryDTO result) {
                    List<IReaderContentDownloaderSubscriber> triggerSubs;
                    loopedExecutor.freeExecutor();
                    synchronized (downloadLock) {
                        if (currentDownloadId != finalId) return;
                        cachedContent.put(finalId, result);
                        triggerSubs = new ArrayList<>(getSubscribersByStoryId(finalId));
                        currentDownloadId = null;
                    }
                    for (IReaderContentDownloaderSubscriber subscriber : triggerSubs) {
                        subscriber.contentLoadSuccess(result);
                    }
                }

                @Override
                public void error(Error<StoryDTO> result) {
                    List<IReaderContentDownloaderSubscriber> triggerSubs;
                    loopedExecutor.freeExecutor();
                    synchronized (downloadLock) {
                        if (currentDownloadId != finalId) return;
                        triggerSubs = new ArrayList<>(getSubscribersByStoryId(finalId));
                        currentDownloadId = null;
                    }
                    for (IReaderContentDownloaderSubscriber subscriber : triggerSubs) {
                        subscriber.contentLoadError();
                    }
                }
            });
        }
    };

    private final LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);

    void init() {
        loopedExecutor.task(invokeQueueTask);
    }

    private List<IReaderContentDownloaderSubscriber> addToQueueOrInvokeCallback(ContentIdAndType id) {
        if (cachedContent.get(id) != null) {
            return getSubscribersByStoryId(id);
        } else {
            currentStoryQueue.push(id);
            return new ArrayList<>();
        }
    }
}
