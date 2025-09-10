package com.inappstory.sdk.game.reader;

import com.inappstory.sdk.core.data.IInAppStoryExtraOptions;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.utils.StringsUtils;

public class GameConfigUserOptions {
    @SerializedName("pos")
    String pos;

    public GameConfigUserOptions() {}

    public GameConfigUserOptions(IInAppStoryExtraOptions extraOptions) {
        this.pos = StringsUtils.escapeSingleQuotes(StringsUtils.getEscapedString(extraOptions.pos()));
    }
}
