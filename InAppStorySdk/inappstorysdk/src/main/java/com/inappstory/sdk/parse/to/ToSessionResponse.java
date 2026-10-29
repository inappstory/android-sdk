package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.SessionResponse;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToSessionResponse extends ToModelParser<SessionResponse> {
    public ToSessionResponse(String value) {
        super(value);
    }

    @Override
    protected SessionResponse parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            SessionResponse sessionResponse = new SessionResponse();
            sessionResponse.isAllowUgc = jsonObj.optBoolean("is_allow_ugc");
            sessionResponse.serverTimestamp = jsonObj.optLongNullable("server_timestamp");
            sessionResponse.preloadGame = jsonObj.optBoolean("preloadGame");
            sessionResponse.previewAspectRatio = (float) jsonObj.optDouble("preview_aspect_ratio");
            sessionResponse.layoutUpdatedAt = jsonObj.optInt("layout_updated_at");
            sessionResponse.isGameReaderDefaultFullscreen =
                    jsonObj.optBoolean("is_game_reader_default_fullscreen");
            sessionResponse.isAllowProfiling = jsonObj.optBoolean("is_allow_profiling");
            sessionResponse.isAllowStatV1 = jsonObj.optBooleanNullable("is_allow_statistic_v1");
            sessionResponse.isAllowStatV2 = jsonObj.optBooleanNullable("is_allow_statistic_v2");
            sessionResponse.isAllowCrash = jsonObj.optBooleanNullable("is_allow_crash");
            sessionResponse.debugSessionKey = jsonObj.optStringNullable("debug_session_key");


            sessionResponse.placeholders =
                    new ToStoryPlaceholderList(jsonObj.optJSONArray("placeholders")).parse();
            sessionResponse.imagePlaceholders =
                    new ToStoryPlaceholderList(jsonObj.optJSONArray("image_placeholders")).parse();
            sessionResponse.sessionAssets =
                    new ToSessionAssetList(jsonObj.optJSONArray("cache")).parse();

            sessionResponse.session = new ToSession(jsonObj.optJSONObject("session")).parse();
            return sessionResponse;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "SessionResponse";
    }
}
