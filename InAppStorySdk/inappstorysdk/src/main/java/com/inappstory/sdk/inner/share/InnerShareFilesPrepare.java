package com.inappstory.sdk.inner.share;

import android.content.Context;

import com.inappstory.sdk.stories.utils.TaskRunner;

import java.util.ArrayList;

public class InnerShareFilesPrepare {
    public void prepareFiles(
            Context context,
            final ShareFilesPrepareCallback callback,
            ArrayList<InnerShareFile> files
    ) {
        new TaskRunner().executeAsync(new FilePathFromBase64(context, files),
                new TaskRunner.Callback<ArrayList<String>>() {
                    @Override
                    public void onComplete(ArrayList<String> result) {
                        callback.onPrepared(result);
                    }
                });
    }
}
