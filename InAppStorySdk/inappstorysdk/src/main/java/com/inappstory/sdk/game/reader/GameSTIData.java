package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.core.inputdialog.InputDialogData;

public class GameSTIData {
    public String id;
    public String cb;
    public GameSTIConfigData config;

    public InputDialogData toInputDialogData() {
        return new InputDialogData(
                config.title,
                config.input.placeholder,
                config.input.type,
                config.button
        );
    }
}
