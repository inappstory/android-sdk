package com.inappstory.sdk.game.cache;

import com.inappstory.sdk.core.IASCore;

public class RemoveOldGameFilesUseCase extends GameNameHolder {
    public RemoveOldGameFilesUseCase(String newUrl) {
        this.newUrl = newUrl;
    }

    String newUrl;

    void remove() {
        String gameName = getGameName(newUrl);
        IASCore.getInstance().filesRepository.removeOldGameFiles(gameName, newUrl);
    }
}
