package com.inappstory.sdk.externalapi.callbacks;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.goods.outercallbacks.ProductCartInteractionCallback;
import com.inappstory.sdk.inappmessage.CloseInAppMessageCallback;
import com.inappstory.sdk.inappmessage.InAppMessageWidgetCallback;
import com.inappstory.sdk.inappmessage.ShowInAppMessageCallback;
import com.inappstory.sdk.inappmessage.ShowInAppMessageSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.errors.ErrorCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickOnShareStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.FavoriteStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.LikeDislikeStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowSlideCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;

public class IASCallbacksExternalAPIImpl implements IASCallbacksExternalAPI {

    private void useCore(UseIASCoreCallback callback) {
        InAppStoryManager.useCore(callback);
    }

    public void error(final ErrorCallback errorCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.ERROR, errorCallback);
            }
        });
    }

    public void clickOnShareStory(final ClickOnShareStoryCallback clickOnShareStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.CLICK_SHARE, clickOnShareStoryCallback);
            }
        });
    }

    public void callToAction(final CallToActionCallback callToActionCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.CALL_TO_ACTION, callToActionCallback);
            }
        });
    }

    public void storyWidget(final StoryWidgetCallback storyWidgetCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.STORY_WIDGET, storyWidgetCallback);
            }
        });
    }

    public void closeStory(final CloseStoryCallback closeStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.CLOSE_STORY, closeStoryCallback);
            }
        });
    }

    public void favoriteStory(final FavoriteStoryCallback favoriteStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.FAVORITE, favoriteStoryCallback);
            }
        });
    }

    public void likeDislikeStory(final LikeDislikeStoryCallback likeDislikeStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.LIKE_DISLIKE, likeDislikeStoryCallback);
            }
        });
    }

    public void showSlide(final ShowSlideCallback showSlideCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.SHOW_SLIDE, showSlideCallback);
            }
        });
    }

    public void showStory(final ShowStoryCallback showStoryCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(IASCallbackType.SHOW_STORY, showStoryCallback);
            }
        });
    }

    public void goodsCartInteraction(
            final ProductCartInteractionCallback productCartInteractionCallback
    ) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(
                        IASCallbackType.PRODUCT_CART_INTERACTION,
                        productCartInteractionCallback
                );
            }
        });
    }

    public void showInAppMessage(final ShowInAppMessageCallback showInAppMessageCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(
                        IASCallbackType.SHOW_IN_APP_MESSAGE,
                        showInAppMessageCallback
                );
            }
        });
    }

    public void showInAppMessageSlide(final ShowInAppMessageSlideCallback showInAppMessageSlideCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(
                        IASCallbackType.SHOW_IN_APP_MESSAGE_SLIDE,
                        showInAppMessageSlideCallback
                );
            }
        });
    }

    public void closeInAppMessage(final CloseInAppMessageCallback closeInAppMessageCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(
                        IASCallbackType.CLOSE_IN_APP_MESSAGE,
                        closeInAppMessageCallback
                );
            }
        });
    }

    public void inAppMessageWidget(final InAppMessageWidgetCallback inAppMessageWidgetCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(
                        IASCallbackType.IN_APP_MESSAGE_WIDGET,
                        inAppMessageWidgetCallback
                );
            }
        });
    }

    @Override
    public void useCallback(final IASCallbackType type, @NonNull final UseIASCallback useIASCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().useCallback(
                        type,
                        useIASCallback
                );
            }
        });
    }

    @Override
    public void setCallback(final IASCallbackType type, final IASCallback useIASCallback) {
        useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().setCallback(
                        type,
                        useIASCallback
                );
            }
        });
    }
}
