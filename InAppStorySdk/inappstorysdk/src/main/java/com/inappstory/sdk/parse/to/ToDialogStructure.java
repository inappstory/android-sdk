package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;


import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.DialogStructure;

import org.json.JSONObject;


public final class ToDialogStructure extends ToModelParser<DialogStructure> {
    public ToDialogStructure(String value) {
        super(value);
    }



    @Override
    protected DialogStructure parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            DialogStructure dialogStructure = new DialogStructure();
            dialogStructure.configV2 = new ToConfigV2(jsonObj.optJSONObject(
                    ModelFields.DialogStructure.configV2
            )).parse();
            dialogStructure.size = new ToSizeStructure(jsonObj.optJSONObject(
                    ModelFields.DialogStructure.size
            )).parse();
            return dialogStructure;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "DialogStructure";
    }
}
