package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.GameConfigOptions;
import com.inappstory.sdk.game.reader.GameDataPlaceholder;
import com.inappstory.sdk.game.reader.SafeAreaInsets;
import com.inappstory.sdk.goods.outercallbacks.ProductCartOffer;
import com.inappstory.sdk.network.annotations.models.SerializedName;

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

        object.put("sessionId", model.sessionId);
        object.put("lang", model.lang);
        object.put("apiKey", model.apiKey);
        object.put("appPackageId", model.appPackageId);
        object.put("deviceId", model.deviceId);
        object.put("userId", model.userId);
        object.put("sdkVersion", model.sdkVersion);
        object.put("apiBaseUrl", model.apiBaseUrl);
        object.put("screenOrientation", model.screenOrientation);
        object.put("userAgent", model.userAgent);
        object.put("fullScreen", model.fullScreen);
        object.put("gameInstanceId", model.gameInstanceId);
        object.put(
                "safeAreaInsets",
                new FromSafeAreaInsets(model.safeAreaInsets).parseToJSONObject()
        );
        if (model.placeholders != null) {
            JSONArray placeholders = new JSONArray();
            for (GameDataPlaceholder placeholder : model.placeholders) {
                placeholders.put(new FromGameDataPlaceholder(placeholder).parseToJSONObject());
            }
            object.put("placeholders", placeholders);
        }
        object.put("variables", new JSONObject(model.userExtraOptions));
        return object;
    }
}
