package com.inappstory.sdk.refactoring.core.downloader;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASAssetsHolder;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.refactoring.core.utils.results.Error;
import com.inappstory.sdk.refactoring.core.utils.results.Result;
import com.inappstory.sdk.refactoring.core.utils.results.Success;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.utils.LoopedExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StorySlidesDownloadManager {

    private final IASCore core;

    private final SlideTaskDownloadQueue firstPriorityTaskKeys = new SlideTaskDownloadQueue();
    private final SlideTaskDownloadQueue secondPriorityTaskKeys = new SlideTaskDownloadQueue();
    private final SlideTaskDownloadQueue commonPriorityTaskKeys = new SlideTaskDownloadQueue();
    private SlideTaskKey currentLoadKey = null;

    private final Object slideTaskKeysLock = new Object();
    private final List<SlideTaskKey> loadedSlides = new ArrayList<>();
    private final Map<SlideTaskKey, SlideTask> tasks = new HashMap<>();
    private final Object subLock = new Object();


    List<IReaderContentDownloaderSubscriber> subscribers = new ArrayList<>();


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


    public StorySlidesDownloadManager(IASCore core) {
        this.core = core;
    }

    public void clear() {
        synchronized (slideTaskKeysLock) {
            loadedSlides.clear();
            firstPriorityTaskKeys.clear();
            secondPriorityTaskKeys.clear();
            commonPriorityTaskKeys.clear();
            currentLoadKey = null;
        }
    }


    private final Runnable invokeQueueTask = new Runnable() {
        @Override
        public void run() {
            SlideTaskKey currentKey;
            SlideTask currentTask;
            synchronized (slideTaskKeysLock) {
                if (currentLoadKey != null) return;
                currentLoadKey = firstPriorityTaskKeys.pop();
                if (currentLoadKey == null) currentLoadKey = secondPriorityTaskKeys.pop();
                if (currentLoadKey == null) currentLoadKey = commonPriorityTaskKeys.pop();
                if (currentLoadKey == null) {
                    loopedExecutor.freeExecutor();
                    return;
                }
                currentTask = tasks.get(currentLoadKey);
                if (currentTask == null) {
                    loopedExecutor.freeExecutor();
                    return;
                }
                currentKey = currentLoadKey;
            }
            Result<Void> downloadResult;
            if (loadedSlides.contains(currentKey)) {
                downloadResult = new Success<>(null);
            } else {
                downloadResult = invokeSlideTask(currentTask);
            }
            loopedExecutor.freeExecutor();
            synchronized (slideTaskKeysLock) {
                if (!Objects.equals(currentKey, currentLoadKey)) return;
                currentLoadKey = null;
            }
            List<IReaderContentDownloaderSubscriber> subscribersById = getSubscribersByStoryId(
                    currentKey.contentIdAndType
            );
            if (downloadResult instanceof Error) {
                for (IReaderContentDownloaderSubscriber subscriber: subscribersById) {
                    subscriber.slideLoadError(currentKey.index);
                }
            } else if (downloadResult instanceof Success) {
                synchronized (slideTaskKeysLock) {
                    loadedSlides.add(currentKey);
                }
                for (IReaderContentDownloaderSubscriber subscriber: subscribersById) {
                    checkBundleResources(subscriber, currentKey.index);
                }
            }
        }
    };

    public void checkBundleResources(
            final IReaderContentDownloaderSubscriber subscriber,
            final int slideIndex
    ) {
        if (core.assetsHolder().assetsIsDownloaded()) {
            subscriber.slideLoadSuccess(slideIndex);
        } else {
            IASAssetsHolder assetsHolder = core.assetsHolder();
            assetsHolder.addAssetsIsReadyCallback(new SessionAssetsIsReadyCallback() {
                @Override
                public void isReady() {
                    subscriber.slideLoadSuccess(slideIndex);
                }

                @Override
                public void assetsIsLoading() {

                }

                @Override
                public void error() {
                    subscriber.slideLoadError(slideIndex);
                }
            });
            assetsHolder.downloadAssets();
        }
    }


    private Result<Void> invokeSlideTask(SlideTask task) {
        try {
            if (!(new LoadSlideUseCase(task, core).loadWithResult())) {
                return new Error<>("");
            } else {
                return new Success<>(null);
            }
        } catch (Exception e) {
            return new Error<>(e.getMessage());
        }
    }


    private final LoopedExecutor loopedExecutor = new LoopedExecutor(100, 100);

    void init() {
        loopedExecutor.init(invokeQueueTask);
    }

    public void revokeTaskPriorities() {
        synchronized (slideTaskKeysLock) {
            List<SlideTaskKey> taskKeys = firstPriorityTaskKeys.retrieve();
            taskKeys.addAll(secondPriorityTaskKeys.retrieve());
            for (SlideTaskKey key : taskKeys) {
                if (Objects.equals(key, currentLoadKey)) continue;
                commonPriorityTaskKeys.push(key);
            }
        }
    }

    public void addTasks(
            IReaderContent content,
            int index,
            ContentType type,
            DownloadContentPriority priority
    ) {
        int maxCount = content.actualSlidesCount();
        if (priority == DownloadContentPriority.SECONDARY)
            maxCount = Math.min(maxCount, 2);
        synchronized (slideTaskKeysLock) {

            for (int i = maxCount - 1; i >= 0; i--) {
                SlideTaskKey key = new SlideTaskKey(
                        new ContentIdAndType(
                                content.id(),
                                type
                        ),
                        i
                );
                if (Objects.equals(currentLoadKey, key)) continue;
                if (!tasks.containsKey(key)) {
                    tasks.put(
                            key,
                            new GenerateSlideTask(core, content, i).generate()
                    );
                }
                if (priority != DownloadContentPriority.PRIMARY || (index == i || i == index - 1 || i == index + 1))
                    putKeyToPriority(key, priority);
                else
                    putKeyToPriority(key, DownloadContentPriority.COMMON);
            }
        }
    }

    private void putKeyToPriority(SlideTaskKey key, DownloadContentPriority priority) {
        if (priority == DownloadContentPriority.PRIMARY) {
            secondPriorityTaskKeys.remove(key);
            commonPriorityTaskKeys.remove(key);
            firstPriorityTaskKeys.push(key);
        } else if (priority == DownloadContentPriority.SECONDARY) {
            firstPriorityTaskKeys.remove(key);
            commonPriorityTaskKeys.remove(key);
            secondPriorityTaskKeys.push(key);
        } else if (priority == DownloadContentPriority.COMMON) {
            if (firstPriorityTaskKeys.contains(key) || secondPriorityTaskKeys.contains(key))
                return;
            commonPriorityTaskKeys.push(key);
        }
    }
}
