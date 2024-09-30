package com.inappstory.sdk.core.ui.screens.gamereader;


import com.inappstory.sdk.core.ui.screens.holder.IOverlapContainerData;
import com.inappstory.sdk.core.ui.screens.holder.OverlapContainerHolderType;

public class GameReaderOverlapContainerData implements IOverlapContainerData {

    @Override
    public OverlapContainerHolderType getHolderType() {
        return OverlapContainerHolderType.GAME;
    }
}
