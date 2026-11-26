package com.inappstory.sdk.core.network.content.usecase;

import com.inappstory.sdk.core.IASCore;

import java.util.List;

public class InAppMessageByIdUseCase extends InAppMessageByIdOrAliasUseCase {
    public InAppMessageByIdUseCase(IASCore core, int id, boolean useTargeting, List<String> tags) {
        super(core, Integer.toString(id), useTargeting, tags);
    }
}
