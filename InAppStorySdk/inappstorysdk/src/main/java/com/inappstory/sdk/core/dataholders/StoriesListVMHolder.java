package com.inappstory.sdk.core.dataholders;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.storieslist.StoriesListVM;
import com.inappstory.sdk.core.storieslist.StoriesListVMState;

import java.util.HashMap;
import java.util.Map;

public class StoriesListVMHolder implements IStoriesListVMHolder {
    private final IASCore core;
    private final Map<String, StoriesListVM> storiesListVMs = new HashMap<>();

    private final Object lock = new Object();

    public StoriesListVMHolder(IASCore core) {
        this.core = core;
    }

    @Override
    public StoriesListVMState getVMState(String uniqueId) {
        if (uniqueId == null) return null;
        synchronized (lock) {
            StoriesListVM storiesListVM = storiesListVMs.get(uniqueId);
            if (storiesListVM != null) return storiesListVM.getState();
            return null;
        }
    }

    @Override
    public void setVMState(String uniqueId, StoriesListVMState state) {
        if (uniqueId == null) return;
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
        if (uniqueId == null) return;
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
