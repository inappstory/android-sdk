package com.inappstory.sdk.core.cache;

class StoryTaskWithPriority {
    int priority;
    int loadType = 0;

    StoryTaskWithPriority(int priority, int loadType) {
        this.priority = priority;
        this.loadType = loadType;
    }

    StoryTaskWithPriority(int loadType) {
        this.loadType = loadType;
    }



    StoryTaskWithPriority() {
    }
    //1 - not loaded, 2 - loading, 3 - loaded,
    // 4 - not loaded partial, 5 - loading partial, 6 - loaded partial
}