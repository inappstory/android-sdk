package io.casestory.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.exceptions.DataException;
import io.casestory.sdk.stories.api.networkclient.ApiClient;
import io.casestory.sdk.stories.api.networkclient.ApiSettings;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.utils.KeyValueStorage;

public class CaseStoryManager {

    private static CaseStoryManager INSTANCE;

    public Context getContext() {
        return context;
    }

    Context context;

    public ArrayList<String> getTags() {
        return tags;
    }

    String getTagsString() {
        if (tags == null) return null;
        return TextUtils.join(",", tags);
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    ArrayList<String> tags;

    public boolean closeOnOverscroll() {
        return closeOnOverscroll;
    }

    public boolean closeOnSwipe() {
        return closeOnSwipe;
    }

    boolean closeOnOverscroll = true;
    boolean closeOnSwipe = true;

    public boolean hasLike() {
        return hasLike;
    }

    public boolean hasShare() {
        return hasShare;
    }

    public boolean hasFavorite() {
        return hasFavorite;
    }

    boolean hasLike = false;
    boolean hasShare = false;
    boolean hasFavorite = false;

    private static final String TEST_DOMAIN = "https://api.casestory.io/";
    private static final String PRODUCT_DOMAIN = "https://api.casestory.io/";

    public String getApiKey() {
        return API_KEY;
    }
    public String getTestKey() {
        return TEST_KEY;
    }

    String API_KEY = "";
    String TEST_KEY = null;

    Intent intent;

    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };


    private CaseStoryManager(Builder builder) {

        KeyValueStorage.setContext(builder.context);
        initManager(builder.context,
                builder.sandbox ? TEST_DOMAIN
                                : PRODUCT_DOMAIN,
                builder.apiKey != null ? builder.apiKey : builder.context
                        .getResources().getString(R.string.narApiKey),
                builder.testKey != null ? builder.testKey : null,
                (builder.userId != null && !builder.userId.isEmpty()) ? builder.userId :
                        "",
                builder.tags != null ? builder.tags : null,
                builder.closeOnOverscroll,
                builder.closeOnSwipe,
                builder.hasFavorite,
                builder.hasLike,
                builder.hasShare);

        if (intent != null) {
            context.unbindService(mConnection);
            mBound = false;
        }
        intent = new Intent(context, CaseStoryService.class);
        context.startService(intent);
    }

    public void setUserId(String userId) throws DataException {
        if (userId.length() < 255) {
            this.userId = userId;
        } else {
            throw new DataException("'userId' can't be longer than 255 characters", new Throwable("CaseStoryManager data is not valid"));
        }
    }

    private String userId;

    public String getUserId() {
        return userId;
    }


    public void setActionBarColor(int actionBarColor) {
        this.actionBarColor = actionBarColor;
    }

    public int actionBarColor = -1;
    List<List<Object>> statistic;

    private void initManager(Context context, String cmsUrl, String apiKey, String testKey, String userId, ArrayList<String> tags,
                             boolean closeOnOverscroll,
                             boolean closeOnSwipe,
                             boolean hasFavorite,
                             boolean hasLike,
                             boolean hasShare) {
        this.context = context;
        this.tags = tags;
        this.closeOnOverscroll = closeOnOverscroll;
        this.closeOnSwipe = closeOnSwipe;
        this.hasFavorite = hasFavorite;
        this.hasLike = hasLike;
        this.hasShare = hasShare;
        this.API_KEY = apiKey;
        this.TEST_KEY = testKey;
        ApiClient.setContext(context);
        this.userId = userId;
       /* if (actionBarColor == -1) {
            actionBarColor = context.getResources().getColor(R.color.nar_readerActionBarColor);
        }*/
//        EventBus.getDefault().register(this);
        statistic = new ArrayList<>();
        if (INSTANCE != null) {
            destroy();
        }
        INSTANCE = this;
        ApiSettings
                .getInstance()
                .cacheDirPath(context.getCacheDir().getAbsolutePath())
                .cmsId("1")
                .cmsKey(this.API_KEY)
                .setWebUrl(cmsUrl)
                .cmsUrl(cmsUrl);

    }

    public static void destroy() {
        if (INSTANCE != null) {
            CaseStoryService.getInstance().logout();
            INSTANCE.context = null;
            INSTANCE.statistic = null;
            KeyValueStorage.removeString("managerInstance");
            try {
               // EventBus.getDefault().unregister(INSTANCE);
            } catch (Exception e) {

            }
        }
        INSTANCE = null;
        StoryDownloader.destroy();
    }



    public static CaseStoryManager getInstance() {
        return INSTANCE;
    }

    public void showPopupStories() {

    }

    public void showStory(int storyId) {

    }

    public static class Builder {

        Context context;

        public boolean sandbox() {
            return sandbox;
        }

        public boolean closeOnOverscroll() {
            return closeOnOverscroll;
        }

        public boolean closeOnSwipe() {
            return closeOnSwipe;
        }

        public boolean hasLike() {
            return hasLike;
        }

        public boolean hasFavorite() {
            return hasFavorite;
        }

        public boolean hasShare() {
            return hasShare;
        }

        public String userId() {
            return userId;
        }

        public String apiKey() {
            return apiKey;
        }

        public String testKey() {
            return testKey;
        }

        public ArrayList<String> tags() {
            return tags;
        }

        boolean sandbox = true;
        boolean closeOnOverscroll = true;
        boolean closeOnSwipe = true;
        boolean hasLike = false;
        boolean hasFavorite = false;
        boolean hasShare = false;
        String userId;
        String apiKey;
        String testKey;
        ArrayList<String> tags;

        public Builder() {
        }

        public Builder context(Context context) throws DataException {
            if (context == null)
                throw new DataException("Context must not be null", new Throwable("CaseStoryManager.Builder data is not valid"));
            Builder.this.context = context;

            return Builder.this;
        }

        public Builder sandbox(boolean sandbox) {
            Builder.this.sandbox = sandbox;
            return Builder.this;
        }


        public Builder closeOnSwipe(boolean closeOnSwipe) {
            Builder.this.closeOnSwipe = closeOnSwipe;
            return Builder.this;
        }

        public Builder closeOnOverscroll(boolean closeOnOverscroll) {
            Builder.this.closeOnOverscroll = closeOnOverscroll;
            return Builder.this;
        }

        public Builder hasFavorite(boolean hasFavorite) {
            Builder.this.hasFavorite = hasFavorite;
            return Builder.this;
        }

        public Builder hasShare(boolean hasShare) {
            Builder.this.hasShare = hasShare;
            return Builder.this;
        }

        public Builder hasLike(boolean hasLike) {
            Builder.this.hasLike = hasLike;
            return Builder.this;
        }

        public Builder apiKey(String apiKey) {
            Builder.this.apiKey = apiKey;
            return Builder.this;
        }

        public Builder testKey(String testKey) {
            Builder.this.testKey = testKey;
            return Builder.this;
        }

        public Builder userId(String userId) throws DataException {
            if (userId.length() < 255) {
                Builder.this.userId = userId;
            } else {
                throw new DataException("'userId' can't be longer than 255 characters", new Throwable("CaseStoryManager.Builder data is not valid"));
            }
            return Builder.this;
        }

        public Builder tags(String... tags) {
            Builder.this.tags = new ArrayList<>();
            for (int i = 1; i < tags.length; i++) {
                Builder.this.tags.add(tags[i]);
            }
            return Builder.this;
        }

        public Builder tags(ArrayList<String> tags) {
            Builder.this.tags = tags;
            return Builder.this;
        }

        public CaseStoryManager create() throws DataException {
            if (Builder.this.context == null) {
                throw new DataException("'context' can't be null", new Throwable("CaseStoryManager.Builder data is not valid"));
            }
            return new CaseStoryManager(Builder.this);
        }
    }
}
