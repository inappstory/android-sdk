package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.GameConfigOptions;
import com.inappstory.sdk.game.reader.GameDataPlaceholder;
import com.inappstory.sdk.game.reader.SafeAreaInsets;
import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FromGameConfigOptions extends ModelToStringParser<GameConfigOptions> {
    public FromGameConfigOptions(GameConfigOptions model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();

        object.put(ModelFields.GameConfigOptions.sessionId, model.sessionId);
        object.put(ModelFields.GameConfigOptions.lang, model.lang);
        object.put(ModelFields.GameConfigOptions.apiKey, model.apiKey);
        object.put(ModelFields.GameConfigOptions.appPackageId, model.appPackageId);
        object.put(ModelFields.GameConfigOptions.deviceId, model.deviceId);
        object.put(ModelFields.GameConfigOptions.userId, model.userId);
        object.put(ModelFields.GameConfigOptions.sdkVersion, model.sdkVersion);
        object.put(ModelFields.GameConfigOptions.apiBaseUrl, model.apiBaseUrl);
        object.put(ModelFields.GameConfigOptions.screenOrientation, model.screenOrientation);
        object.put(ModelFields.GameConfigOptions.userAgent, model.userAgent);
        object.put(ModelFields.GameConfigOptions.fullScreen, model.fullScreen);
        object.put(ModelFields.GameConfigOptions.gameInstanceId, model.gameInstanceId);
        object.put(
                ModelFields.GameConfigOptions.safeAreaInsets,
                new FromSafeAreaInsets(model.safeAreaInsets).parseToJSONObject()
        );
        if (model.placeholders != null) {
            JSONArray placeholders = new JSONArray();
            for (GameDataPlaceholder placeholder : model.placeholders) {
                placeholders.put(new FromGameDataPlaceholder(placeholder).parseToJSONObject());
            }
            object.put(ModelFields.GameConfigOptions.placeholders, placeholders);
        }
        object.put(ModelFields.GameConfigOptions.userExtraOptions, new JSONObject(model.userExtraOptions));
        return object;
    }
}
