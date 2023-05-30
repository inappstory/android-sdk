package com.inappstory.sdk.inner.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.inappstory.sdk.stories.utils.TaskRunner;

import java.util.ArrayList;

public class InnerShareFilesPrepare {
    public void prepareFiles(
            Context context,
            final ShareFilesPrepareCallback callback,
            ArrayList<InnerShareFile> files
    ) {
        new TaskRunner().executeAsync(new UriFromBase64(context, files),
                new TaskRunner.Callback<ArrayList<Uri>>() {
                    @Override
                    public void onComplete(ArrayList<Uri> result) {
                        callback.onPrepared(result);
                    }
                });
    }
}
