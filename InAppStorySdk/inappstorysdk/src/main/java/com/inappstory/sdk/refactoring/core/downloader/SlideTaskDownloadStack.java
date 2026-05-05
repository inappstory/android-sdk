package com.inappstory.sdk.refactoring.core.downloader;


public class SlideTaskDownloadStack extends DownloadStack<SlideTaskKey> {
    @Override
    public SlideTaskKey pop() {
        SlideTaskKey resKey = null;
        for (SlideTaskKey key: ids) {
            if (resKey == null || key.index < resKey.index) {
                resKey = key;
            }
        }
        ids.remove(resKey);
        return resKey;
    }
}