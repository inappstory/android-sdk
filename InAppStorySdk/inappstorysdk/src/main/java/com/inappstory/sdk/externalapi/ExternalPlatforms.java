package com.inappstory.sdk.externalapi;

public enum ExternalPlatforms {
    NONE("InAppStorySDK"),
    FLUTTER_SDK("InAppStoryFlutterSDK"),
    REACT_NATIVE_SDK("InAppStoryReactNativeSDK"),
    KMP_SDK("InAppStoryKMPSDK");

    private final String prefix;

    ExternalPlatforms(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}
