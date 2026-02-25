package com.inappstory.sdk.refactoring.session.data.local;

import java.util.ArrayList;
import java.util.List;

public class SessionDTO {
    public SessionDTO(
            String sessionId,
            boolean isAllowUgc,
            boolean preloadGame,
            float previewAspectRatio,
            boolean isAllowProfiling,
            boolean isAllowStatV1,
            boolean isAllowStatV2,
            boolean isAllowCrash
    ) {
        this.sessionId = sessionId;
        this.isAllowUgc = isAllowUgc;
        this.preloadGame = preloadGame;
        this.previewAspectRatio = previewAspectRatio;
        this.isAllowProfiling = isAllowProfiling;
        this.isAllowStatV1 = isAllowStatV1;
        this.isAllowStatV2 = isAllowStatV2;
        this.isAllowCrash = isAllowCrash;
    }

    private final String sessionId;
    private final boolean isAllowUgc;

    public String sessionId() {
        return sessionId;
    }

    public boolean isAllowUgc() {
        return isAllowUgc;
    }

    public boolean preloadGame() {
        return preloadGame;
    }

    public float previewAspectRatio() {
        return previewAspectRatio;
    }

    public List<SessionAssetDTO> sessionAssets() {
        return sessionAssets;
    }

    public boolean isAllowProfiling() {
        return isAllowProfiling;
    }

    public boolean isAllowStatV1() {
        return isAllowStatV1;
    }

    public boolean isAllowStatV2() {
        return isAllowStatV2;
    }

    public boolean isAllowCrash() {
        return isAllowCrash;
    }

    public List<SessionPlaceholderDTO> placeholders() {
        return placeholders;
    }

    public List<SessionPlaceholderDTO> imagePlaceholders() {
        return imagePlaceholders;
    }

    private final boolean preloadGame;
    private final float previewAspectRatio;

    public void sessionAssets(List<SessionAssetDTO> sessionAssets) {
        this.sessionAssets = new ArrayList<>(sessionAssets);
    }

    public void placeholders(List<SessionPlaceholderDTO> placeholders) {
        this.placeholders = new ArrayList<>(placeholders);
    }

    public void imagePlaceholders(List<SessionPlaceholderDTO> imagePlaceholders) {
        this.imagePlaceholders = new ArrayList<>(imagePlaceholders);
    }

    private List<SessionAssetDTO> sessionAssets = new ArrayList<>();
    private final boolean isAllowProfiling;
    private final boolean isAllowStatV1;
    private final boolean isAllowStatV2;
    private final boolean isAllowCrash;
    private List<SessionPlaceholderDTO> placeholders = new ArrayList<>();
    private List<SessionPlaceholderDTO> imagePlaceholders = new ArrayList<>();
}
