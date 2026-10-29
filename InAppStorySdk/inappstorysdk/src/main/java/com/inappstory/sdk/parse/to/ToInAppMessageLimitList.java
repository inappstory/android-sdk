package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;

import org.json.JSONArray;

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
                InAppMessageLimit limit = new ToInAppMessageLimit(limits.optJSONObject(i)).parse();
                if (limit == null) return null;
                inAppMessageLimits.add(limit);
            }
            return inAppMessageLimits;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "List of InAppMessageLimit";
    }
}
