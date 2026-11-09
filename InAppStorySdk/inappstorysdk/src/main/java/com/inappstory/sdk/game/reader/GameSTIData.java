package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.core.inputdialog.InputDialogData;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameSTIData {
    @SerializedName(ModelFields.GameSTIData.id)
    public String id;
    @SerializedName(ModelFields.GameSTIData.cb)
    public String cb;
    @SerializedName(ModelFields.GameSTIData.config)
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
