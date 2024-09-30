package com.inappstory.sdk.core.stories;

public class StoriesListVM {

    private StoriesListVMState state;

    public StoriesListVMState getState() {
        return state;
    }

    public void setState(StoriesListVMState state) {
        this.state = state;
    }

    public StoriesListVM(StoriesListVMState state) {
        this.state = state;
    }
}
