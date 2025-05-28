package com.inappstory.sdk.core.network.content.usecase;

import android.text.TextUtils;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASLimitsHolder;
import com.inappstory.sdk.core.data.IInAppMessageLimit;
import com.inappstory.sdk.core.inappmessages.InAppMessagesLimitCallback;
import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;
import com.inappstory.sdk.network.callbacks.NetworkCallback;
import com.inappstory.sdk.stories.api.models.InAppMessageLimitListType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InAppMessageLimitsUseCase {
    private final IASCore core;
    private final List<Integer> ids;

    public InAppMessageLimitsUseCase(
            IASCore core,
            List<Integer> ids
    ) {
        this.core = core;
        this.ids = ids;
    }

    public void loadLimits(final InAppMessagesLimitCallback callback) {
        if (ids == null || ids.isEmpty()) {
            callback.error();
            return;
        }
        List<Integer> localIds = new ArrayList<>(ids);
        Collections.sort(localIds);
        final String idsInString = TextUtils.join(",", localIds);
        final IASLimitsHolder limitsHolder = core.limitsHolder();
        List<IInAppMessageLimit> cachedLimits = limitsHolder.cachedLimitForIds(idsInString);
        if (cachedLimits != null && !cachedLimits.isEmpty()) {
            callback.success(cachedLimits);
        } else {
            core.network().enqueue(
                    core.network().getApi().getInAppMessagesLimits(
                            idsInString
                    ),
                    new NetworkCallback<List<InAppMessageLimit>>() {
                        @Override
                        public void onSuccess(List<InAppMessageLimit> response) {
                            if (response == null || response.isEmpty()) {
                                callback.error();
                                return;
                            }
                            List<IInAppMessageLimit> limits = new ArrayList<>();
                            limits.addAll(response);
                            limitsHolder.addLimitToCache(idsInString, limits);
                            callback.success(limits);
                        }

                        @Override
                        public Type getType() {
                            return new InAppMessageLimitListType();
                        }
                    }
            );
        }

    }
}
