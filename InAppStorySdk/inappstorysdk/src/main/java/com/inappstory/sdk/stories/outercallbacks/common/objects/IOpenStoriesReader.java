package com.inappstory.sdk.stories.outercallbacks.common.objects;


import android.content.Context;
import android.os.Bundle;

public interface IOpenStoriesReader {
    void onOpen(
            Context context,
            Bundle bundle
    );
}
