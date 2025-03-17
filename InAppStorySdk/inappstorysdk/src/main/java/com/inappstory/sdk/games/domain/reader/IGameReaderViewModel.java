package com.inappstory.sdk.games.domain.reader;

import com.inappstory.sdk.inappmessage.domain.stedata.STETypeAndData;
import com.inappstory.sdk.stories.utils.Observer;
import com.inappstory.sdk.stories.utils.SingleTimeEvent;

public interface IGameReaderViewModel extends IGameReaderViewModelJS {
    void addSubscriber(Observer<GameReaderState> observer);
    void removeSubscriber(Observer<GameReaderState> observer);

    void readerIsOpened(boolean fromScratch);
    void readerIsClosing();
    void closeReader();

    void initState(GameReaderState state);
    GameReaderState getCurrentState();
    void updateCurrentUiState(GameReaderUIState newState);
    void updateCurrentLoadState(GameReaderLoadState newState);

    SingleTimeEvent<STETypeAndData> singleTimeEvents();


}
