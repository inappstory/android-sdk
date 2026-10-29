package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.PreloadGameCenterData;
import com.inappstory.sdk.stories.api.models.WebResource;

import org.json.JSONObject;


public final class ToPreloadGameCenterData extends ToModelParser<PreloadGameCenterData> {
    public ToPreloadGameCenterData(JSONObject value) {
        super(value);
    }

    @Override
    protected PreloadGameCenterData parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            PreloadGameCenterData pgcd = new PreloadGameCenterData();
            pgcd.id = jsonObj.getString("id");
            pgcd.url = jsonObj.getString("downloadUrl");
            pgcd.archiveSize = jsonObj.optLongNullable("archiveSize");
            pgcd.archiveUncompressedSize = jsonObj.optLongNullable("archiveUncompressedSize");
            pgcd.archiveSha1 = jsonObj.optStringNullable("archiveSha1");
            pgcd.archiveItems =
                    new ToGameArchiveItemList(jsonObj.optJSONArray("archiveItems")).parse();
            pgcd.resources =
                    new ToWebResourceList(jsonObj.optJSONArray("resources")).parse();
            pgcd.splashScreen =
                    new ToGameSplashScreen(jsonObj.optJSONObject("splashScreen")).parse();
            pgcd.splashLandscapeScreen =
                    new ToGameSplashScreen(jsonObj.optJSONObject("splashLandscapeScreen")).parse();
            pgcd.splashAnimation =
                    new ToGameSplashAnimation(jsonObj.optJSONObject("splashLottieScreen")).parse();
            return pgcd;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "PreloadGameCenterData";
    }
}
