package com.inappstory.sdk.stories.uidomain.reader.views.storiesdisplay;

import androidx.lifecycle.LiveData;

public interface IStoriesWebViewDisplayViewModel extends IStoriesDisplayViewModel {
    LiveData<String> evaluateJSCalls();
    LiveData<String> loadUrlCalls();
    LiveData<SlideContentState> slideContentState();
}
