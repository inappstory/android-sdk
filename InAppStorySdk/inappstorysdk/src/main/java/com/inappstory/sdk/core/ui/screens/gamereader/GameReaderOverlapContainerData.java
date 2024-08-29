package com.inappstory.sdk.core.ui.screens.gamereader;


import com.inappstory.sdk.core.ui.screens.IOverlapContainerData;
import com.inappstory.sdk.core.ui.screens.OverlapContainerHolderType;

public class GameReaderOverlapContainerData implements IOverlapContainerData {

    @Override
    public OverlapContainerHolderType getHolderType() {
        return OverlapContainerHolderType.GAME;
    }
}
