package com.inappstory.sdk.stories.ui.reader;

public class ActiveStoryItem {
    public ActiveStoryItem(int listIndex, String uniqueListId) {
        this.listIndex = listIndex;
        this.uniqueListId = uniqueListId;
    }

    public int getListIndex() {
        return listIndex;
    }

    public String getUniqueListId() {
        return uniqueListId;
    }

    private int listIndex;


    private String uniqueListId;
}
