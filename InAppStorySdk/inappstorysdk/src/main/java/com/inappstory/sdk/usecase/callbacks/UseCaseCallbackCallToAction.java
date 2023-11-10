package com.inappstory.sdk.usecase.callbacks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

public class UseCaseCallbackCallToAction implements IUseCaseCallbackWithContext {
    private String deeplink;
    private SlideData slideData;
    private ClickAction clickAction;

    @Override
    public void invoke(Context context) {
        if (CallbackManager.getInstance().getCallToActionCallback() != null) {
            CallbackManager.getInstance().getCallToActionCallback().callToAction(
                    context,
                    slideData,
                    deeplink,
                    clickAction
            );
        } else if (CallbackManager.getInstance().getUrlClickCallback() != null) {
            CallbackManager.getInstance().getUrlClickCallback().onUrlClick(deeplink);
        } else {
            if (IASCore.getInstance().notConnected()) {
                if (CallbackManager.getInstance().getErrorCallback() != null) {
                    CallbackManager.getInstance().getErrorCallback().noConnection();
                }
                return;
            }
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(deeplink));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch (Exception e) {

            }
        }
    }

    public UseCaseCallbackCallToAction(
            String deeplink,
            SlideData slideData,
            ClickAction clickAction
    ) {
        this.deeplink = deeplink;
        this.slideData = slideData;
        this.clickAction = clickAction;
    }
}
