package com.inappstory.sdk.stories.cache;

class StoryTask {
    int priority;
    int loadType = 0;

    StoryTask(int priority, int loadType) {
        this.priority = priority;
        this.loadType = loadType;
    }

    StoryTask() {
    }
    //1 - not loaded, 2 - loading, 3 - loaded,
    // 4 - not loaded partial, 5 - loading partial, 6 - loaded partial
}