package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToInAppMessage extends ToModelParser<InAppMessage> {
    public ToInAppMessage(String value) {
        super(value);
    }

    public ToInAppMessage(JSONObject value) {
        super(value);
    }

    @Override
    protected InAppMessage parseObject(JSONObject jsonObject) {
        return super.parseObject(jsonObject);
    }

    @Override
    protected InAppMessage parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            InAppMessage inAppMessage = new InAppMessage();
            inAppMessage.id = jsonObj.getInt("id");
            inAppMessage.hasPlaceholders = jsonObj.optBoolean("has_placeholder");
            inAppMessage.hasLimit = jsonObj.optBoolean("has_limit");
            inAppMessage.displayTo = jsonObj.optLongNullable("display_to");
            inAppMessage.displayFrom = jsonObj.optLongNullable("display_from");
            inAppMessage.frequencyLimit = jsonObj.optLongNullable("frequency_limit");
            inAppMessage.campaignName = jsonObj.optStringNullable("campaign_name");
            inAppMessage.hasSwipeUp = jsonObj.optBooleanNullable("has_swipe_up");
            inAppMessage.disableClose = jsonObj.optBoolean("disable_close");
            inAppMessage.screenType = jsonObj.optInt("type");

            inAppMessage.customAttributes = JsonParser.toMap(jsonObj.optJSONObject("custom_attributes"));
            inAppMessage.layoutTemplateVariables = JsonParser.toMap(jsonObj.optJSONObject("layout_template_variables"));
            inAppMessage.appearance = JsonParser.toObjectMap(jsonObj.optJSONObject("appearance"));

            inAppMessage.slides = new ToInAppMessageSlideList(jsonObj.optJSONArray("slides")).parse();
            inAppMessage.events = new ToInAppMessageEventList(jsonObj.optJSONArray("events")).parse();
            return inAppMessage;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "InAppMessage";
    }
}
