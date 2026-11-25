package com.inappstory.sdk.core.network.content.usecase;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASDataSettingsHolder;
import com.inappstory.sdk.core.inappmessages.InAppMessageByIdCallback;
import com.inappstory.sdk.core.network.content.RequestFields;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.core.utils.ConnectionCheck;
import com.inappstory.sdk.core.utils.ConnectionCheckCallback;
import com.inappstory.sdk.network.NetworkClient;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.network.models.RequestLocalParameters;
import com.inappstory.sdk.parse.to.ToInAppMessage;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.TargetingBodyObject;
import com.inappstory.sdk.stories.api.models.callbacks.OpenSessionCallback;

import java.util.ArrayList;
import java.util.List;

public class InAppMessageByIdUseCase extends InAppMessageByIdOrSlugUseCase {
    public InAppMessageByIdUseCase(IASCore core, int id, boolean useTargeting, List<String> tags) {
        super(core, Integer.toString(id), useTargeting, tags);
    }
}
