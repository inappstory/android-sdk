package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.StoryLoadedData;
import com.inappstory.sdk.stories.api.models.WriteClipboardData;

import org.json.JSONObject;


public final class ToWriteClipboardData extends ToModelParser<WriteClipboardData> {
    public ToWriteClipboardData(String value) {
        super(value);
    }



    @Override
    protected WriteClipboardData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            WriteClipboardData data = new WriteClipboardData();
            data.type = jsonObj.optStringNullable(
                    ModelFields.WriteClipboardData.type
            );
            data.textValue = jsonObj.optStringNullable(
                    ModelFields.WriteClipboardData.textValue
            );
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "WriteClipboardData";
    }
}
