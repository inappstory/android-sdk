package com.inappstory.sdk.stories.outercallbacks.common.gamereader;

public interface GameCallback {
    void startGame(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index);
    void finishGame(int id,
                    String title,
                    String tags,
                    int slidesCount,
                    int index,
                    String result);
    void closeGame(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index);
}
