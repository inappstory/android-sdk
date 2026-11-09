package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.ConfigV2;

import org.json.JSONObject;


public final class ToConfigV2 extends ToModelParser<ConfigV2> {
    public ToConfigV2(JSONObject value) {
        super(value);
    }

    @Override
    public ConfigV2 parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            ConfigV2 config = new ConfigV2();
            config.main = new ToMainV2(jsonObj.optJSONObject(
                    ModelFields.ConfigV2.main
            )).parse();
            config.factor = (float) jsonObj.optDouble(
                    ModelFields.ConfigV2.factor
            );
            return config;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "ConfigV2";
    }
}
