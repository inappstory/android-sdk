package com.inappstory.sdk.core;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.URLUtil;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.repository.statistic.IStatisticV1Repository;
import com.inappstory.sdk.core.repository.statistic.StatisticV1Repository;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoriesPreviewsCallback;
import com.inappstory.sdk.core.repository.stories.interfaces.IGetStoryCallback;
import com.inappstory.sdk.core.utils.lrudiskcache.FileManager;
import com.inappstory.sdk.core.utils.lrudiskcache.LruDiskCache;
import com.inappstory.sdk.core.utils.network.NetworkClient;
import com.inappstory.sdk.core.repository.files.FilesRepository;
import com.inappstory.sdk.core.repository.files.IFilesRepository;
import com.inappstory.sdk.core.repository.game.GameRepository;
import com.inappstory.sdk.core.repository.game.IGameRepository;
import com.inappstory.sdk.core.repository.session.interfaces.IGetSessionCallback;
import com.inappstory.sdk.core.repository.session.ISessionRepository;
import com.inappstory.sdk.core.repository.session.SessionRepository;
import com.inappstory.sdk.core.repository.session.dto.SessionDTO;
import com.inappstory.sdk.core.repository.session.dto.UgcEditorDTO;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.StoriesRepository;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.core.models.ImagePlaceholderValue;
import com.inappstory.sdk.core.models.api.Story.StoryType;
import com.inappstory.sdk.core.models.StoryPlaceholder;
import com.inappstory.sdk.core.cache.StoryDownloadManager;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.IShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.screen.DefaultOpenStoriesReader;
import com.inappstory.sdk.stories.outercallbacks.screen.IOpenStoriesReader;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.uidomain.list.listnotify.ChangeUserIdListNotify;
import com.inappstory.sdk.utils.StringsUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IASCore {
    private static IASCore INSTANCE;
    private static final Object lock = new Object();
    public final static int TAG_LIMIT = 4000;

    public static IASCore getInstance() {
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new IASCore();
            return INSTANCE;
        }
    }


    public Map<String, String> getPlaceholders() {
        InAppStoryManager manager = InAppStoryManager.getInstance();
        if (manager != null)
            return manager.getPlaceholdersCopy();
        return new HashMap<>();
    }

    public void saveSessionPlaceholders(List<StoryPlaceholder> placeholders) {
        if (placeholders == null) return;
        InAppStoryManager.getInstance().setDefaultPlaceholders(placeholders);
    }

    public Map<String, Pair<ImagePlaceholderValue, ImagePlaceholderValue>> getImagePlaceholdersValuesWithDefaults() {
        return InAppStoryManager.getInstance().getImagePlaceholdersValuesWithDefaults();
    }

    public void saveSessionImagePlaceholders(List<StoryPlaceholder> placeholders) {
        if (placeholders == null) return;
        for (StoryPlaceholder placeholder : placeholders) {
            if (!URLUtil.isNetworkUrl(placeholder.defaultVal))
                continue;
            String key = placeholder.name;
            ImagePlaceholderValue defaultVal = ImagePlaceholderValue.createByUrl(placeholder.defaultVal);
            InAppStoryManager.getInstance().setDefaultImagePlaceholder(key,
                    defaultVal);
        }
    }

    public StoryDownloadManager downloadManager = new StoryDownloadManager();

    public IFilesRepository filesRepository;

    public ISessionRepository sessionRepository;

    private IStoriesRepository storiesRepository;

    private IStoriesRepository ugcStoriesRepository;

    public IStatisticV1Repository statisticV1Repository;

    private List<ChangeUserIdListNotify> changeUserIdListNotifies = new ArrayList<>();

    private final Object changeUserIdLock = new Object();

    public void addChangeUserIdListNotify(ChangeUserIdListNotify notify) {
        synchronized (changeUserIdLock) {
            changeUserIdListNotifies.add(notify);
        }
    }

    public void removeChangeUserIdListNotify(ChangeUserIdListNotify notify) {
        synchronized (changeUserIdLock) {
            changeUserIdListNotifies.remove(notify);
        }
    }

    public IGameRepository gameRepository;

    private ListNotifier listNotifier = new ListNotifier();

    public ListNotifier getListNotifier() {
        if (listNotifier == null) listNotifier = new ListNotifier();
        return listNotifier;
    }

    //TODO remove this method
    public LruDiskCache getInfiniteCache() {
        return filesRepository.getInfiniteCache();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId != null && this.userId != null && !this.userId.equals(userId)) {
            this.userId = userId;
            getStoriesRepository(StoryType.COMMON).clearCachedLists();
            getStoriesRepository(StoryType.UGC).clearCachedLists();
            downloadManager.cleanTasks();
            closeSession();
            synchronized (changeUserIdLock) {
                for (ChangeUserIdListNotify notify : changeUserIdListNotifies) {
                    notify.onChange();
                }
            }
        } else {
            this.userId = userId;
        }

    }

    private String userId;


    private String lastSingleOpen = null;

    public void showSingleStory(
            final String storyId,
            final boolean once,
            final Context context,
            final AppearanceManager manager,
            final IShowStoryCallback callback,
            final Integer slide,
            final StoryType type,
            final SourceType readerSource,
            final int readerAction
    ) {
        if (this.userId == null || StringsUtils.getBytesLength(this.userId) > 255) {
            InAppStoryManager.showELog(
                    InAppStoryManager.IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            context,
                            R.string.ias_setter_user_length_error
                    )
            );
            return;
        }
        if (lastSingleOpen != null &&
                lastSingleOpen.equals(storyId)) return;
        lastSingleOpen = storyId;
        getStoriesRepository(type).getStoryByStringId(
                storyId,
                once,
                new IGetStoryCallback<IStoryDTO>() {
                    @Override
                    public void onSuccess(IStoryDTO story) {
                        if (story != null) {
                            try {
                                int c = Integer.parseInt(lastSingleOpen);
                                if (c != story.getId())
                                    return;
                            } catch (Exception ignored) {

                            }
                            if (callback != null)
                                callback.onShow();
                            ArrayList<Integer> stIds = new ArrayList<>();
                            stIds.add(story.getId());
                            ScreensManager.getInstance().openStoriesReader(
                                    context,
                                    null,
                                    manager,
                                    stIds,
                                    0,
                                    readerSource,
                                    readerAction,
                                    slide,
                                    null,
                                    StoryType.COMMON
                            );
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    lastSingleOpen = null;
                                }
                            }, 1000);
                        } else {
                            if (callback != null)
                                callback.onError();
                            lastSingleOpen = null;
                        }
                    }

                    @Override
                    public void onError() {
                        if (callback != null)
                            callback.onError();
                        lastSingleOpen = null;
                    }
                }
        );
    }

    public void showOnboardingStories(
            final Integer limit,
            final String feed,
            final List<String> tags,
            final List<String> currentTags,
            final Context outerContext,
            final AppearanceManager manager
    ) {
        if (tags != null && StringsUtils.getBytesLength(TextUtils.join(",", tags)) > TAG_LIMIT) {
            InAppStoryManager.showELog(
                    InAppStoryManager.IAS_ERROR_TAG,
                    StringsUtils.getErrorStringFromContext(
                            outerContext,
                            R.string.ias_setter_tags_length_error
                    )
            );
            return;
        }
        String tagsString = null;
        if (tags != null) {
            tagsString = TextUtils.join(",", tags);
        } else if (currentTags != null) {
            tagsString = TextUtils.join(",", currentTags);
        }

        getStoriesRepository(StoryType.COMMON).getOnboardingStoriesAsync(
                feed,
                limit,
                tagsString,
                new IGetStoriesPreviewsCallback() {
                    @Override
                    public void onSuccess(List<IPreviewStoryDTO> response) {
                        showLoadedOnboardings(response, outerContext, manager, feed);
                    }

                    @Override
                    public void onError() {

                    }
                }
        );
    }

    private void showLoadedOnboardings(
            final List<IPreviewStoryDTO> response,
            final Context outerContext,
            final AppearanceManager manager,
            final String feed
    ) {
        if (response == null || response.size() == 0) {
            if (CallbackManager.getInstance().getOnboardingLoadCallback() != null) {
                CallbackManager.getInstance().getOnboardingLoadCallback().onboardingLoad(
                        0,
                        StringsUtils.getNonNull(feed)
                );
            }
            return;
        }
        ArrayList<Integer> storiesIds = new ArrayList<>();
        for (IPreviewStoryDTO story : response) {
            storiesIds.add(story.getId());
        }
        ScreensManager.getInstance().openStoriesReader(
                outerContext,
                null,
                manager,
                storiesIds,
                0,
                SourceType.ONBOARDING,
                feed,
                StoryType.COMMON
        );
        if (CallbackManager.getInstance().getOnboardingLoadCallback() != null) {
            CallbackManager.getInstance().getOnboardingLoadCallback().onboardingLoad(
                    response.size(),
                    StringsUtils.getNonNull(feed)
            );
        }
    }

    public IStoriesRepository getStoriesRepository(StoryType type) {
        if (type == StoryType.UGC) return ugcStoriesRepository;
        return storiesRepository;
    }

    public void getSession(
            IGetSessionCallback<SessionDTO> callback
    ) {
        if (userId == null) {
            callback.onError();
            return;
        }
        sessionRepository.getSession(userId, callback);
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    private NetworkClient networkClient;


    public IOpenStoriesReader getOpenStoriesReader() {
        return openStoriesReader;
    }

    public void setOpenStoriesReader(IOpenStoriesReader openStoriesReader) {
        this.openStoriesReader = openStoriesReader;
    }

    private IOpenStoriesReader openStoriesReader = new DefaultOpenStoriesReader();


    public void closeSession() {
        sessionRepository.closeSession();
    }

    public void getUgcEditor(
            final IGetSessionCallback<UgcEditorDTO> callback
    ) {
        sessionRepository.getSession(userId, new IGetSessionCallback<SessionDTO>() {
            @Override
            public void onSuccess(SessionDTO session) {
                callback.onSuccess(sessionRepository.getUgcEditor());
            }

            @Override
            public void onError() {
                callback.onError();
            }
        });
    }

    public boolean getSendNewStatistic() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return false;
        return inAppStoryManager.isSendStatistic()
                && sessionRepository.isAllowStatV2();
    }

    public boolean getSendStatistic() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return false;
        return inAppStoryManager.isSendStatistic()
                && sessionRepository.isAllowStatV1();
    }

    public void init(Context context, int cacheSizeType) {
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "Stories"));
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "temp"));
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        new ImageLoader(context);
        if (sessionRepository == null) {
            sessionRepository = new SessionRepository(context);
            storiesRepository = new StoriesRepository(StoryType.COMMON);
            ugcStoriesRepository = new StoriesRepository(StoryType.UGC);
            gameRepository = new GameRepository();
            statisticV1Repository = new StatisticV1Repository();
        }
        if (filesRepository == null) {
            filesRepository = new FilesRepository(context.getCacheDir(), cacheSizeType);
        }
    }

    public void initFilesRepository(Context context, int cacheSizeType) {
        if (filesRepository == null) {
            filesRepository = new FilesRepository(context.getCacheDir(), cacheSizeType);
        }
    }

    public void init(Context context) {
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "Stories"));
        FileManager.deleteRecursive(new File(context.getFilesDir() + File.separator + "temp"));
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        new ImageLoader(context);
        soundOn = !context.getResources().getBoolean(R.bool.defaultMuted);
        if (sessionRepository == null) {
            sessionRepository = new SessionRepository(context);
            storiesRepository = new StoriesRepository(StoryType.COMMON);
            ugcStoriesRepository = new StoriesRepository(StoryType.UGC);
            gameRepository = new GameRepository();
            statisticV1Repository = new StatisticV1Repository();
        }

    }

    ConnectivityManager connectivityManager;

    public boolean isSoundOn() {
        return soundOn;
    }

    private boolean soundOn = false;

    public void changeSoundStatus() {
        this.soundOn = !soundOn;
    }

    public void setSoundStatus(boolean soundOn) {
        this.soundOn = soundOn;
    }


    public boolean notConnected() {
        if (connectivityManager == null) return true;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network nw = connectivityManager.getActiveNetwork();
                if (nw == null) return true;
                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                return actNw == null || (!actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                        !actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
                        !actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) &&
                        !actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
            } else {
                NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
                return nwInfo == null || !nwInfo.isConnected();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sharingProcess = false;
    private static final Object shareLock = new Object();

    public boolean isShareProcess() {
        synchronized (shareLock) {
            return sharingProcess;
        }
    }

    public void isShareProcess(boolean sharingProcess) {
        synchronized (shareLock) {
            this.sharingProcess = sharingProcess;
        }
    }

}
