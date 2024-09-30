package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.stories.StoriesListVM;
import com.inappstory.sdk.core.stories.StoriesListVMState;

import java.util.HashMap;

public class StoriesListVMHolder implements IStoriesListVMHolder {
    private HashMap<String, StoriesListVM> storiesListVMs;

    private final Object lock = new Object();

    @Override
    public StoriesListVMState getVMState(String uniqueId) {
        synchronized (lock) {
            StoriesListVM storiesListVM = storiesListVMs.get(uniqueId);
            if (storiesListVM != null) return storiesListVM.getState();
            return null;
        }
    }

    @Override
    public void setVMState(String uniqueId, StoriesListVMState state) {
        synchronized (lock) {
            StoriesListVM storiesListVM = storiesListVMs.get(uniqueId);
            if (storiesListVM == null) {
                storiesListVMs.put(uniqueId, new StoriesListVM(state));
            } else {
                storiesListVM.setState(state);
            }
        }
    }

    @Override
    public void removeVM(String uniqueId) {
        synchronized (lock) {
            storiesListVMs.remove(uniqueId);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            storiesListVMs.clear();
        }
    }
}
