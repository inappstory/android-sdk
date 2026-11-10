package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameCenterData;

import org.json.JSONObject;


public final class ToGameCenterData extends ToModelParser<GameCenterData> {
    public ToGameCenterData(String value) {
        super(value);
    }

    @Override
    protected GameCenterData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            GameCenterData gameCenterData = new GameCenterData();
            gameCenterData.id = jsonObj.optStringNullable(
                    ModelFields.GameCenterData.id
            );
            gameCenterData.layoutDirection = jsonObj.optStringNullable(
                    ModelFields.GameCenterData.layoutDirection
            );
            gameCenterData.loggerLevel = jsonObj.optIntNullable(
                    ModelFields.GameCenterData.loggerLevel
            );
            gameCenterData.url = jsonObj.getString(
                    ModelFields.GameCenterData.url
            );
            gameCenterData.initCode = jsonObj.getString(
                    ModelFields.GameCenterData.initCode
            );
            gameCenterData.canTryReloadCount = jsonObj.optIntNullable(
                    ModelFields.GameCenterData.canTryReloadCount
            );
            gameCenterData.archiveSize = jsonObj.optLongNullable(
                    ModelFields.GameCenterData.archiveSize
            );
            gameCenterData.archiveUncompressedSize = jsonObj.optLongNullable(

                    ModelFields.GameCenterData.archiveUncompressedSize
            );
            gameCenterData.archiveSha1 = jsonObj.optStringNullable(
                    ModelFields.GameCenterData.archiveSha1
            );

            gameCenterData.archiveItems =
                    new ToGameArchiveItemList(jsonObj.optJSONArray(

                            ModelFields.GameCenterData.archiveItems
                    )).parse();
            gameCenterData.resources =
                    new ToWebResourceList(jsonObj.optJSONArray(
                            ModelFields.GameCenterData.resources
                    )).parse();
            gameCenterData.splashScreen =
                    new ToGameSplashScreen(jsonObj.optJSONObject(
                            ModelFields.GameCenterData.splashScreen
                    )).parse();
            gameCenterData.splashLandscapeScreen =
                    new ToGameSplashScreen(jsonObj.optJSONObject(
                            ModelFields.GameCenterData.splashLandscapeScreen
                    )).parse();
            gameCenterData.options =
                    new ToGameScreenOptions(jsonObj.optJSONObject(
                            ModelFields.GameCenterData.options
                    )).parse();
            gameCenterData.gameLaunchConditions =
                    new ToGameLaunchConditions(jsonObj.optJSONObject(
                            ModelFields.GameCenterData.gameLaunchConditions
                    )).parse();
            gameCenterData.splashAnimation =
                    new ToGameSplashAnimation(jsonObj.optJSONObject(
                            ModelFields.GameCenterData.splashAnimation
                    )).parse();

            gameCenterData.instanceUserData = JsonParser.toObjectMap(jsonObj.optJSONObject(
                    ModelFields.GameCenterData.instanceUserData
            ));
            return gameCenterData;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameCenterData";
    }
}
