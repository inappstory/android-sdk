package com.inappstory.sdk.core.network.content.models;


import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import java.util.ArrayList;

/**
 * Created by paperrose on 19.02.2018.
 */

public class Session {
    @Required
    @SerializedName(ModelFields.Session.id)
    public String id;
}
