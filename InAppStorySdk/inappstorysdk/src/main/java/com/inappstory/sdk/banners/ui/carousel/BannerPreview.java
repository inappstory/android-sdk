package com.inappstory.sdk.banners.ui.carousel;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerPlaceViewModelsHolder;
import com.inappstory.sdk.core.banners.BannerPreviewViewModel;
import com.inappstory.sdk.core.banners.BannerWidgetViewModelType;

public class BannerPreview extends BannerCarousel {
    public BannerPreview(@NonNull Context context) {
        super(context);
    }

    public BannerPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViewModel() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerPreview.this.core = core;
                BannerPlaceViewModelsHolder holder = core
                        .widgetViewModels()
                        .bannerPlaceViewModels();
                bannerCarouselViewModel =
                        (BannerPreviewViewModel) holder.getOrCreate(
                                uniqueId(),
                                BannerWidgetViewModelType.PAGER
                        );
            }
        });
    }
}
