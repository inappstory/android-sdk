package com.inappstory.sdk.core.api.impl;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASCallbacks;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.inappmessage.CloseInAppMessageCallback;
import com.inappstory.sdk.inappmessage.InAppMessageLoadCallback;
import com.inappstory.sdk.inappmessage.InAppMessageWidgetCallback;
import com.inappstory.sdk.inappmessage.ShowInAppMessageCallback;
import com.inappstory.sdk.stories.callbacks.ExceptionCallback;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;
import com.inappstory.sdk.stories.outercallbacks.common.onboarding.OnboardingLoadCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outercallbacks.common.single.SingleLoadCallback;

public class IASCallbacksImpl implements IASCallbacks {
    private final IASCore core;

    public IASCallbacksImpl(IASCore core) {
        this.core = core;
    }

    public void share(ShareCallback shareCallback) {
        this.shareCallback = shareCallback;
    }


    @Override
    public void useCallback(
            IASCallbackType type,
            @NonNull UseIASCallback useIASCallback
    ) {
        switch (type) {
            case EXCEPTION:
                if (exceptionCallback != null) {
                    useIASCallback.use(exceptionCallback);
                    return;
                }
                break;
            case ERROR:
                if (errorCallback != null) {
                    useIASCallback.use(errorCallback);
                    return;
                }
                break;
            case FAVORITE:
                if (favoriteStoryCallback != null) {
                    useIASCallback.use(favoriteStoryCallback);
                    return;
                }
                break;
            case SHOW_SLIDE:
                if (showSlideCallback != null) {
                    useIASCallback.use(showSlideCallback);
                    return;
                }
                break;
            case STORY_WIDGET:
                if (storyWidgetCallback != null) {
                    useIASCallback.use(storyWidgetCallback);
                    return;
                }
                break;
            case SHOW_STORY:
                if (showStoryCallback != null) {
                    useIASCallback.use(showStoryCallback);
                    return;
                }
                break;
            case CLOSE_STORY:
                if (closeStoryCallback != null) {
                    useIASCallback.use(closeStoryCallback);
                    return;
                }
                break;
            case LIKE_DISLIKE:
                if (likeDislikeStoryCallback != null) {
                    useIASCallback.use(likeDislikeStoryCallback);
                    return;
                }
                break;
            case ONBOARDING:
                if (onboardingLoadCallback != null) {
                    useIASCallback.use(onboardingLoadCallback);
                    return;
                }
                break;
            case SINGLE:
                if (singleLoadCallback != null) {
                    useIASCallback.use(singleLoadCallback);
                    return;
                }
                break;
            case SHARE_ADDITIONAL:
                if (shareCallback != null) {
                    useIASCallback.use(shareCallback);
                    return;
                }
                break;
            case GAME_READER:
                if (gameReaderCallback != null) {
                    useIASCallback.use(gameReaderCallback);
                    return;
                }
                break;
            case CLICK_SHARE:
                if (clickOnShareStoryCallback != null) {
                    useIASCallback.use(clickOnShareStoryCallback);
                    return;
                }
                break;
            case CALL_TO_ACTION:
                if (callToActionCallback != null) {
                    useIASCallback.use(callToActionCallback);
                    return;
                }
                break;
            case IN_APP_MESSAGE_LOAD:
                if (inAppMessageLoadCallback != null) {
                    useIASCallback.use(inAppMessageLoadCallback);
                    return;
                }
                break;
            case SHOW_IN_APP_MESSAGE:
                if (showInAppMessageCallback != null) {
                    useIASCallback.use(showInAppMessageCallback);
                    return;
                }
                break;
            case CLOSE_IN_APP_MESSAGE:
                if (closeInAppMessageCallback != null) {
                    useIASCallback.use(closeInAppMessageCallback);
                    return;
                }
                break;
            case IN_APP_MESSAGE_WIDGET:
                if (inAppMessageWidgetCallback != null) {
                    useIASCallback.use(inAppMessageWidgetCallback);
                    return;
                }
                break;
            default:
                break;
        }
        useIASCallback.onDefault();
    }

    @Override
    public void setCallback(IASCallbackType type, IASCallback callback) {
        switch (type) {
            case ERROR:
                errorCallback = (ErrorCallback) callback;
                return;
            case EXCEPTION:
                exceptionCallback = (ExceptionCallback) callback;
                return;
            case FAVORITE:
                favoriteStoryCallback = (FavoriteStoryCallback) callback;
                return;
            case SHOW_SLIDE:
                showSlideCallback = (ShowSlideCallback) callback;
                return;
            case STORY_WIDGET:
                storyWidgetCallback = (StoryWidgetCallback) callback;
                return;
            case SHOW_STORY:
                showStoryCallback = (ShowStoryCallback) callback;
                return;
            case CLOSE_STORY:
                closeStoryCallback = (CloseStoryCallback) callback;
                return;
            case LIKE_DISLIKE:
                likeDislikeStoryCallback = (LikeDislikeStoryCallback) callback;
                return;
            case ONBOARDING:
                onboardingLoadCallback = (OnboardingLoadCallback) callback;
                return;
            case SINGLE:
                singleLoadCallback = (SingleLoadCallback) callback;
                return;
            case SHARE_ADDITIONAL:
                shareCallback = (ShareCallback) callback;
                return;
            case CALL_TO_ACTION:
                callToActionCallback = (CallToActionCallback) callback;
                return;
            case CLICK_SHARE:
                clickOnShareStoryCallback = (ClickOnShareStoryCallback) callback;
                return;
            case GAME_READER:
                gameReaderCallback = (GameReaderCallback) callback;
                return;
            case IN_APP_MESSAGE_LOAD:
                inAppMessageLoadCallback = (InAppMessageLoadCallback) callback;
                return;
            case SHOW_IN_APP_MESSAGE:
                showInAppMessageCallback = (ShowInAppMessageCallback) callback;
                return;
            case CLOSE_IN_APP_MESSAGE:
                closeInAppMessageCallback = (CloseInAppMessageCallback) callback;
                return;
            case IN_APP_MESSAGE_WIDGET:
                inAppMessageWidgetCallback = (InAppMessageWidgetCallback) callback;
                return;
            default:
                break;
        }
    }


    private ExceptionCallback exceptionCallback;
    private ErrorCallback errorCallback;
    private GameReaderCallback gameReaderCallback;
    private OnboardingLoadCallback onboardingLoadCallback;
    private StoryWidgetCallback storyWidgetCallback;
    private ClickOnShareStoryCallback clickOnShareStoryCallback;
    private CallToActionCallback callToActionCallback;
    private CloseStoryCallback closeStoryCallback;
    private FavoriteStoryCallback favoriteStoryCallback;
    private LikeDislikeStoryCallback likeDislikeStoryCallback;
    private InAppMessageLoadCallback inAppMessageLoadCallback;
    private ShowInAppMessageCallback showInAppMessageCallback;
    private CloseInAppMessageCallback closeInAppMessageCallback;
    private InAppMessageWidgetCallback inAppMessageWidgetCallback;
    private ShowSlideCallback showSlideCallback;
    private ShowStoryCallback showStoryCallback;
    private SingleLoadCallback singleLoadCallback;
    private ShareCallback shareCallback;

}
