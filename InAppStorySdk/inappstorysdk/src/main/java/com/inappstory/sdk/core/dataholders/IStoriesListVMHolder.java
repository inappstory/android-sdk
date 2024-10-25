package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.storieslist.StoriesListVMState;

public interface IStoriesListVMHolder {
    StoriesListVMState getVMState(String uniqueId);
    void setVMState(String uniqueId, StoriesListVMState state);
    void removeVM(String uniqueId);
    void clear();
}
