package com.inappstory.sdk.core.network.content.mock;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.core.network.content.models.BannerPlace;
import com.inappstory.sdk.core.network.content.models.BannerSlide;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.core.network.content.models.InAppMessageEvent;
import com.inappstory.sdk.core.network.content.models.InAppMessageSlide;
import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MockBanners {
    Random random = new Random();

    public MockBanners() {

    }

    public Banner getMockBanner(InAppMessage inAppMessage) {
        Banner banner = new Banner();
        banner.id = random.nextInt(1000);
        banner.appearance = new HashMap<>();
        banner.appearance.put("single_banner_aspect_ratio", 2f);
        banner.appearance.put("corner_radius", 16);
        banner.appearance.put("loop", true);
        banner.appearance.put("autoplay", false);
        banner.appearance.put("autoplay_delay", 1000);
        banner.appearance.put("animation_speed", 300);
        banner.slides = new ArrayList<>();
        banner.slides.add(
                convertIAMToBannerSlide(
                        inAppMessage
                )
        );
        banner.layout = inAppMessage.layout;
        return banner;
    }

    private BannerSlide convertIAMToBannerSlide(InAppMessage inAppMessage) {
        InAppMessageSlide slide = inAppMessage.slides.get(0);
        BannerSlide bannerSlide = new BannerSlide();
        bannerSlide.slideIndex = 0;
        bannerSlide.slidePayload = slide.slidePayload;
        bannerSlide.html = slide.html;
        bannerSlide.resources = slide.resources;
        bannerSlide.placeholders = slide.placeholders;
        bannerSlide.duration = 1000;
        return bannerSlide;
    }

    public BannerPlace getMockBannerPlace(List<InAppMessage> inAppMessageList, String place) {
        BannerPlace bannerPlace = new BannerPlace();
        bannerPlace.id = 1;
        bannerPlace.banners = new ArrayList<>();
        int i = 0;
        for (InAppMessage inAppMessage : inAppMessageList) {
            if (inAppMessage.belongsToEvent(place)) {
                bannerPlace.banners.add(getMockBanner(inAppMessage));
                i++;
                if (i > 10) break;
            }
        }
        return bannerPlace;
    }
}
