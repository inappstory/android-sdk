package com.inappstory.sdk.core.cache;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.models.api.Story.StoryType;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoryDownloadManager {
    public void changePriority(int storyId, List<Integer> adjacent, StoryType type) {
        if (slidesDownloader != null)
            slidesDownloader.changePriority(storyId, adjacent, type);
    }

    public void changePriorityForSingle(int storyId, StoryType type) {
        if (slidesDownloader != null)
            slidesDownloader.changePriorityForSingle(storyId, type);

    }

    public void initDownloaders() {
        storyDownloader.init();
        slidesDownloader.init();
    }

    public void destroy() {
        storyDownloader.destroy();
        slidesDownloader.destroy();
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    public void cleanTasks() {
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
    }

    public void clearCache() {
        storyDownloader.cleanTasks();
        slidesDownloader.cleanTasks();
        IASCore.getInstance().getStoriesRepository(StoryType.COMMON).clearAll();
        IASCore.getInstance().getStoriesRepository(StoryType.UGC).clearAll();
        IASCore.getInstance().filesRepository.clearCaches();
    }


    private final Object lock = new Object();
    List<ReaderPageManager> subscribers = new ArrayList<>();

    public void addSubscriber(ReaderPageManager manager) {
        synchronized (lock) {
            subscribers.add(manager);
        }
    }

    public void removeSubscriber(ReaderPageManager manager) {
        synchronized (lock) {
            subscribers.remove(manager);
        }
    }

    void slideLoaded(SlideTaskData key) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == key.storyId && subscriber.getStoryType() == key.storyType) {
                    subscriber.slideLoadedInCache(key.index);
                    return;
                }
            }
        }
    }


    void storyError(StoryTaskData storyTaskData) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == storyTaskData.storyId) {
                    subscriber.storyLoadError();
                    return;
                }
            }
        }
    }

    void slideError(SlideTaskData slideTaskData) {
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == slideTaskData.storyId) {
                    subscriber.slideLoadError(slideTaskData.index);
                    return;
                }
            }
        }
    }

    void storyLoaded(int storyId, StoryType type) {
        Log.e("updateProgress",
                "storyLoaded " + storyId
        );
        synchronized (lock) {
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber.getStoryId() == storyId && subscriber.getStoryType() == type) {
                    subscriber.storyLoadedInCache();
                    return;
                }
            }
        }
    }

    public int checkIfPageLoaded(int storyId, int index, StoryType type) {
        try {
            return slidesDownloader.checkIfPageLoaded(new SlideTaskData(storyId, index, type));
        } catch (IOException e) {
            return 0;
        }
    }

    private void downloadFileOrAlter(
            final UrlWithAlter url,
            final IDownloadPageFileCallback pageFileCallback
    ) {
        IASCore.getInstance()
                .filesRepository
                .getStoryFile(
                        url.getUrl(),
                        new IFileDownloadCallback() {
                            @Override
                            public void onSuccess(String fileAbsolutePath) {
                                pageFileCallback.download(
                                        url,
                                        DownloadPageFileStatus.SUCCESS
                                );
                            }

                            @Override
                            public void onError(int errorCode, String error) {
                                if (url.getAlter() != null) {
                                    downloadFileOrAlter(
                                            new UrlWithAlter(url.getAlter(), null),
                                            pageFileCallback
                                    );
                                } else {
                                    pageFileCallback.download(
                                            url,
                                            DownloadPageFileStatus.ERROR
                                    );
                                }
                            }
                        }
                );
    }

    public StoryDownloadManager() {
        this.storyDownloader = new StoryDownloader(new DownloadStoryCallback() {
            @Override
            public void onDownload(IStoryDTO story, int loadType, StoryType type) {
                storyLoaded(story.getId(), type);
                try {
                    slidesDownloader.addStoryPages(story, loadType, type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(StoryTaskData storyTaskData) {
                storyError(storyTaskData);
            }
        }, StoryDownloadManager.this);

        this.slidesDownloader = new SlidesDownloader(
                new IDownloadPageFile() {
                    @Override
                    public void downloadFile(
                            UrlWithAlter url,
                            IDownloadPageFileCallback pageFileCallback
                    ) {
                        try {
                            downloadFileOrAlter(url, pageFileCallback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(StoryTaskData storyTaskData) {
                        storyError(storyTaskData);
                    }

                    @Override
                    public void onSlideError(SlideTaskData taskData) {
                        slideError(taskData);
                        storyDownloader.setStoryLoadType(
                                new StoryTaskData(
                                        taskData.storyId,
                                        taskData.storyType
                                ),
                                -2);
                    }
                },
                StoryDownloadManager.this
        );
    }

    public void addStoryTask(int storyId, ArrayList<Integer> addIds, StoryType type) {
        try {
            storyDownloader.addStoryTask(storyId, addIds, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reloadStory(int storyId, StoryType type) {
        slidesDownloader.removeSlideTasks(new StoryTaskData(storyId, type));
        storyDownloader.reload(storyId, new ArrayList<Integer>(), type);
    }

    private StoryDownloader storyDownloader;
    private SlidesDownloader slidesDownloader;
}
