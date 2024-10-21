package com.inappstory.sdk;

import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.dataholders.IListItemContent;
import com.inappstory.sdk.externalapi.subscribers.InAppStoryAPISubscribersManager;
import com.inappstory.sdk.stories.api.models.Image;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.logs.ExceptionLog;
import com.inappstory.sdk.stories.exceptions.ExceptionManager;
import com.inappstory.sdk.stories.stackfeed.StackStoryObserver;
import com.inappstory.sdk.stories.ui.list.StoryFavoriteImage;
import com.inappstory.sdk.stories.ui.list.ListManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InAppStoryService {

    public static InAppStoryService getInstance() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager == null) return null;
        return manager.iasCore().inAppStoryService();
    }

    public static void useInstance(@NonNull UseServiceInstanceCallback callback) {
        InAppStoryService inAppStoryService = getInstance();
        try {
            if (inAppStoryService != null) {
                callback.use(inAppStoryService);
            } else {
                callback.error();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final IASCore core;

    public InAppStoryService(IASCore core) {
        this.core = core;
    }

    public InAppStoryAPISubscribersManager getApiSubscribersManager() {
        return apiSubscribersManager;
    }

    private InAppStoryAPISubscribersManager apiSubscribersManager;


    ListReaderConnector connector = new ListReaderConnector();

    public ListReaderConnector getListReaderConnector() {
        if (connector == null) connector = new ListReaderConnector();
        return connector;
    }

    public class ListReaderConnector {
        public void changeStory(final int storyId, final String listID, final boolean shownOnlyNewStories) {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (StackStoryObserver storyObserver : stackStoryObservers.values()) {
                        Log.e("changeStory", storyId + " " + listID);
                        storyObserver.onUpdate(storyId, listID, shownOnlyNewStories);
                    }
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.changeStory(storyId, listID);
                    }
                    IListItemContent story =
                            core.contentHolder()
                                    .listsContent()
                                    .getByIdAndType(storyId, ContentType.STORY);
                    if (story instanceof Story)
                        apiSubscribersManager.openStory(story.id(), listID);
                }
            });
        }

        public void readerIsClosed() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.readerIsClosed();
                    }
                    apiSubscribersManager.readerIsClosed();
                }
            });
        }

        public void readerIsOpened() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.readerIsOpened();
                    }
                    apiSubscribersManager.readerIsOpened();
                }
            });

        }

        public void userIdChanged() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.userIdChanged();
                    }
                    service.apiSubscribersManager.refreshAllLists();
                }
            });
        }

        public void sessionIsOpened(final String currentSessionId) {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.sessionIsOpened(currentSessionId);
                    }
                }
            });
        }

        public void storyFavorite(final int id, final boolean favStatus) {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    IListItemContent story = core.contentHolder()
                            .listsContent().getByIdAndType(
                                    id, ContentType.STORY
                            );
                    if (story == null) return;
                    if (favStatus) {
                        core.contentHolder().favoriteItems().setByIdAndType(
                                new StoryFavoriteImage(
                                        id,
                                        story.imageCoverByQuality(Image.QUALITY_MEDIUM),
                                        story.backgroundColor()
                                ),
                                id,
                                ContentType.STORY
                        );
                    } else {
                        core.contentHolder().favoriteItems().setByIdAndType(
                                null,
                                id,
                                ContentType.STORY
                        );
                    }
                    boolean isEmpty = core.contentHolder().favoriteItems().isEmpty(ContentType.STORY);
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.storyFavorite(id, favStatus, isEmpty);
                    }
                    service.apiSubscribersManager.storyFavorite();
                }
            });
        }

        public void clearAllFavorites() {
            useInstance(new UseServiceInstanceCallback() {
                @Override
                public void use(@NonNull InAppStoryService service) {
                    for (ListManager sub : service.getListSubscribers()) {
                        sub.clearAllFavorites();
                    }
                    service.apiSubscribersManager.clearAllFavorites();
                }
            });
        }
    }

    Set<ListManager> listSubscribers;
    final HashMap<String, StackStoryObserver> stackStoryObservers = new HashMap<>();
    public static Set<ListManager> tempListSubscribers;

    public Set<ListManager> getListSubscribers() {
        if (listSubscribers == null) listSubscribers = new HashSet<>();
        return listSubscribers;
    }

    public void subscribeStackStoryObserver(String key, StackStoryObserver observer) {
        synchronized (stackStoryObservers) {
            stackStoryObservers.put(key, observer);
        }
    }

    public void unsubscribeStackStoryObserver(StackStoryObserver observer) {
        synchronized (stackStoryObservers) {
            stackStoryObservers.remove(observer);
        }
    }

    public static void checkAndAddListSubscriber(final ListManager listManager) {
        useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) {
                service.addListSubscriber(listManager);
            }

            @Override
            public void error() {
                if (tempListSubscribers == null) tempListSubscribers = new HashSet<>();
                tempListSubscribers.add(listManager);
            }
        });
    }

    public void addListSubscriber(ListManager listManager) {
        if (listSubscribers == null) listSubscribers = new HashSet<>();
        listSubscribers.add(listManager);
    }


    public void clearSubscribers() {
        for (ListManager listManager : listSubscribers) {
            listManager.clear();
        }
        synchronized (stackStoryObservers) {
            stackStoryObservers.clear();
        }
        tempListSubscribers.clear();
        listSubscribers.clear();
    }


    public void removeListSubscriber(ListManager listManager) {
        if (listSubscribers == null) return;
        listManager.clear();
        if (tempListSubscribers != null)
            tempListSubscribers.remove(listManager);
        listSubscribers.remove(listManager);
    }

    public static void createExceptionLog(final Throwable throwable) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                ExceptionManager em = new ExceptionManager(core);
                ExceptionLog el = em.generateExceptionLog(throwable);
                em.saveException(el);
                em.sendException(el);
            }
        });
    }

    public void onCreate() {
        this.apiSubscribersManager = new InAppStoryAPISubscribersManager(core);
        if (tempListSubscribers != null) {
            if (listSubscribers == null) listSubscribers = new HashSet<>();
            InAppStoryManager.debugSDKCalls("IASService_subscribers", "temp size:" + tempListSubscribers.size() + " / size:" + listSubscribers.size());
            listSubscribers.addAll(tempListSubscribers);
            tempListSubscribers.clear();
        }

    }

}
