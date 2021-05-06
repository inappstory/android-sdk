package com.inappstory.sdk.stories.callbacks;

public class CallbackManager {

    private UrlClickCallback urlClickCallback;
    private AppClickCallback appClickCallback;
    private ShareCallback shareCallback;

    public void setUrlClickCallback(UrlClickCallback urlClickCallback) {
        this.urlClickCallback = urlClickCallback;
    }

    public void setAppClickCallback(AppClickCallback appClickCallback) {
        this.appClickCallback = appClickCallback;
    }

    public void setShareCallback(ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
    }

    public ShareCallback getShareCallback() {
        return shareCallback;
    }


    public UrlClickCallback getUrlClickCallback() {
        return urlClickCallback;
    }

    public AppClickCallback getAppClickCallback() {
        return appClickCallback;
    }

    private CallbackManager() {

    }

    private static volatile CallbackManager INSTANCE;


    public static CallbackManager getInstance() {
        if (INSTANCE == null) {
            synchronized (CallbackManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new CallbackManager();
            }
        }
        return INSTANCE;
    }


}
