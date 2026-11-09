package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
            inAppMessage.id = jsonObj.getInt(
                    ModelFields.InAppMessage.id
            );
            inAppMessage.hasPlaceholders = jsonObj.optBoolean(
                    ModelFields.InAppMessage.hasPlaceholders
            );
            inAppMessage.hasLimit = jsonObj.optBoolean(
                    ModelFields.InAppMessage.hasLimit
            );
            inAppMessage.displayTo = jsonObj.optLongNullable(
                    ModelFields.InAppMessage.displayTo
            );
            inAppMessage.displayFrom = jsonObj.optLongNullable(
                    ModelFields.InAppMessage.displayFrom
            );
            inAppMessage.frequencyLimit = jsonObj.optLongNullable(
                    ModelFields.InAppMessage.frequencyLimit
            );
            inAppMessage.campaignName = jsonObj.optStringNullable(
                    ModelFields.InAppMessage.campaignName
            );
            inAppMessage.hasSwipeUp = jsonObj.optBooleanNullable(
                    ModelFields.InAppMessage.hasSwipeUp
            );
            inAppMessage.disableClose = jsonObj.optBoolean(
                    ModelFields.InAppMessage.disableClose
            );
            inAppMessage.screenType = jsonObj.optInt(
                    ModelFields.InAppMessage.screenType
            );

            inAppMessage.customAttributes = JsonParser.toMap(jsonObj.optJSONObject(
                    ModelFields.InAppMessage.customAttributes
            ));
            inAppMessage.layoutTemplateVariables = JsonParser.toMap(jsonObj.optJSONObject(
                    ModelFields.InAppMessage.layoutTemplateVariables
            ));
            inAppMessage.appearance = JsonParser.toObjectMap(jsonObj.optJSONObject(
                    ModelFields.InAppMessage.appearance
            ));

            inAppMessage.slides = new ToInAppMessageSlideList(jsonObj.optJSONArray(
                    ModelFields.InAppMessage.slides
            )).parse();
            inAppMessage.events = new ToInAppMessageEventList(jsonObj.optJSONArray(
                    ModelFields.InAppMessage.events
            )).parse();
            return inAppMessage;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "InAppMessage";
    }
}
