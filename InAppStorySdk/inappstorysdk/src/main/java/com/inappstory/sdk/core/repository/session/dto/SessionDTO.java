package com.inappstory.sdk.core.repository.session.dto;


public class SessionDTO {
    public SessionDTO(
            String id,
            String userId,
            long expireIn,
            float previewAspectRatio
    ) {
        this.expireIn = expireIn;
        this.userId = userId;
        this.previewAspectRatio = previewAspectRatio;
        this.id = id;
    }

    public long getExpireIn() {
        return expireIn;
    }

    public float getPreviewAspectRatio() {
        return previewAspectRatio;
    }

    public String getId() {
        return id;
    }

    private long expireIn;

    public String getUserId() {
        return userId;
    }

    private String userId;
    private float previewAspectRatio;
    private String id;
}
