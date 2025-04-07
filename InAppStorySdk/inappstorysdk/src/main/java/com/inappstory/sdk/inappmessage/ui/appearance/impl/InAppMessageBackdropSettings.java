package com.inappstory.sdk.inappmessage.ui.appearance.impl;

import com.inappstory.sdk.inappmessage.ui.appearance.InAppMessageBackdrop;
import com.inappstory.sdk.utils.format.NumberUtils;

import java.util.Map;

public class InAppMessageBackdropSettings implements InAppMessageBackdrop {
    public InAppMessageBackdropSettings(
            float alpha
    ) {
        this.alpha = alpha;
    }

    public InAppMessageBackdropSettings(Map<String, Object> appearance) {
        if (appearance == null) return;
        String alphaKey = "alpha";
        NumberUtils numberUtils = new NumberUtils();
        if (appearance.containsKey(alphaKey)) {
            alpha = numberUtils.convertNumberToFloat(appearance.get(alphaKey));
        }
    }

    public InAppMessageBackdropSettings() {
    }

    private Float alpha;

    @Override
    public float alpha() {
        return alpha != null ? alpha : 0.3f;
    }
}
