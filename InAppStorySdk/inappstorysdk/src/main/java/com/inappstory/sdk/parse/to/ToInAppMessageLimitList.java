package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public final class ToInAppMessageLimitList extends ToModelParser<List<InAppMessageLimit>> {
    public ToInAppMessageLimitList(String value) {
        super(value);
    }

    @Override
    protected List<InAppMessageLimit> parseString(@NonNull String value) {
        try {
            JSONArray limits = new JSONArray(value);
            List<InAppMessageLimit> inAppMessageLimits = new ArrayList<>();
            for (int i = 0; i < limits.length(); i++) {
                inAppMessageLimits.add()
            }

            return inAppMessageLimits;
        } catch (Exception e) {
            return null;
        }
    }
}
