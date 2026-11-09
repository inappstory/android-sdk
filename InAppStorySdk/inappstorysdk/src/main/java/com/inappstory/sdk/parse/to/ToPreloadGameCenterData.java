package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            pgcd.id = jsonObj.getString(
                    ModelFields.PreloadGameCenterData.id
            );
            pgcd.url = jsonObj.getString(
                    ModelFields.PreloadGameCenterData.url
            );
            pgcd.archiveSize = jsonObj.optLongNullable(
                    ModelFields.PreloadGameCenterData.archiveSize
            );
            pgcd.archiveUncompressedSize = jsonObj.optLongNullable(
                    ModelFields.PreloadGameCenterData.archiveUncompressedSize
            );
            pgcd.archiveSha1 = jsonObj.optStringNullable(
                    ModelFields.PreloadGameCenterData.archiveSha1
            );
            pgcd.archiveItems =
                    new ToGameArchiveItemList(jsonObj.optJSONArray(
                            ModelFields.PreloadGameCenterData.archiveItems
                    )).parse();
            pgcd.resources =
                    new ToWebResourceList(jsonObj.optJSONArray(
                            ModelFields.PreloadGameCenterData.resources
                    )).parse();
            pgcd.splashScreen =
                    new ToGameSplashScreen(jsonObj.optJSONObject(
                            ModelFields.PreloadGameCenterData.splashScreen
                    )).parse();
            pgcd.splashLandscapeScreen =
                    new ToGameSplashScreen(jsonObj.optJSONObject(
                            ModelFields.PreloadGameCenterData.splashLandscapeScreen
                    )).parse();
            pgcd.splashAnimation =
                    new ToGameSplashAnimation(jsonObj.optJSONObject(
                            ModelFields.PreloadGameCenterData.splashAnimation
                    )).parse();
            return pgcd;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "PreloadGameCenterData";
    }
}
