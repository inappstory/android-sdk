package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.parse.ExtendedJsonObject;
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
            gameCenterData.id = jsonObj.getString("id");
            gameCenterData.layoutDirection = jsonObj.optStringNullable("languageDirection");
            gameCenterData.loggerLevel = jsonObj.optIntNullable("loggerLevel");
            gameCenterData.url = jsonObj.getString("downloadUrl");
            gameCenterData.initCode = jsonObj.getString("initCode");
            gameCenterData.canTryReloadCount = jsonObj.optIntNullable("canTryReloadCount");
            gameCenterData.archiveSize = jsonObj.optLongNullable("archiveSize");
            gameCenterData.archiveSha1 = jsonObj.optStringNullable("archiveSha1");

            gameCenterData.archiveItems =
                    new ToGameArchiveItemList(jsonObj.optJSONArray("archiveItems")).parse();
            gameCenterData.resources =
                    new ToWebResourceList(jsonObj.optJSONArray("resources")).parse();
            gameCenterData.splashScreen =
                    new ToGameSplashScreen(jsonObj.optJSONObject("splashScreen")).parse();
            gameCenterData.splashLandscapeScreen =
                    new ToGameSplashScreen(jsonObj.optJSONObject("splashLandscapeScreen")).parse();
            gameCenterData.options =
                    new ToGameScreenOptions(jsonObj.optJSONObject("screenOptions")).parse();
            gameCenterData.gameLaunchConditions =
                    new ToGameLaunchConditions(jsonObj.optJSONObject("launchConditions")).parse();
            gameCenterData.splashAnimation =
                    new ToGameSplashAnimation(jsonObj.optJSONObject("splashLottieScreen")).parse();

            return gameCenterData;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "GameCenterData";
    }
}
