package io.casestory.sdk.stories.api.models.callbacks;

import android.view.View;

import java.util.Map;

public interface GetListItem {
    View getItem();
    View getReadedItem();
    Map<String, Integer> getBinds();
}
