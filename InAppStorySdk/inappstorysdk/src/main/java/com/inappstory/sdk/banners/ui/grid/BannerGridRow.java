package com.inappstory.sdk.banners.ui.grid;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.banners.ui.banner.BannerInit;
import com.inappstory.sdk.banners.ui.banner.BannerView;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerDownloadManager;
import com.inappstory.sdk.core.banners.BannerViewModel;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.banners.IBannersWidgetViewModel;
import com.inappstory.sdk.core.banners.ICustomBannerGridAppearance;
import com.inappstory.sdk.core.data.IBanner;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.List;

public class BannerGridRow extends LinearLayout {

    public BannerGridRow(Context context) {
        super(context);
        init(context);
    }

    public BannerGridRow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerGridRow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {

        setOrientation(LinearLayout.HORIZONTAL);
    }

    private boolean checkVisibility() {
        Rect rowRect = new Rect();
        getGlobalVisibleRect(rowRect);
        Rect displayRect = new Rect();
        getWindowVisibleDisplayFrame(displayRect);
        return rowRect.top <= displayRect.bottom && rowRect.bottom >= displayRect.top;
    }

    public void updateVisibility() {
        boolean newVisibility = checkVisibility();
        if (currentVisibility == newVisibility) return;
        currentVisibility = newVisibility;
        int count = getChildCount();
        try {
            for (int i = 0; i < getChildCount(); i++) {
                BannerView bannerView = ((BannerView) getChildAt(i));
                if (newVisibility)
                    if (bannersStarted)
                        bannerView.resumeBanner();
                    else {
                        bannerView.startBanner();
                    }
                else
                    bannerView.pauseBanner();
            }
            if (count > 0 && newVisibility)
                bannersStarted = true;
        } catch (Exception e) {

        }

    }

    private boolean currentVisibility = false;
    private boolean bannersStarted = false;

    public void setViews(
            IASCore core,
            int startIndex,
            int rootWidth,
            String iterationId,
            String bannerGridId,
            ICustomBannerGridAppearance appearance,
            @NonNull List<IBanner> banners
    ) {
        removeAllViews();
        int columns = Math.min(appearance.columnCount(), banners.size() - startIndex);
        int maxColumns = appearance.columnCount();
        setWeightSum(maxColumns);
        IBannersWidgetViewModel bannerGridViewModel = core
                .widgetViewModels()
                .bannerPlaceViewModels()
                .get(bannerGridId);
        if (columns == 0) return;
        float minRatio = banners.get(0).bannerAppearance().singleBannerAspectRatio();
        for (int i = 1; i < columns; i++) {
            minRatio = Math.min(minRatio,
                    banners.get(startIndex + i).bannerAppearance().singleBannerAspectRatio()
            );
        }
        int maxBannerWidth =
                (rootWidth - Sizes.dpToPxExt(appearance.horizontalGap(), getContext())) / maxColumns;
        for (int i = 0; i < columns; i++) {
            int position = startIndex + i;
            IBanner banner = banners.get(position);
            IBannerViewModel bannerViewModel = bannerGridViewModel.getBannerViewModel(
                    banner.id(),
                    position
            );
            bannerViewModel.iterationId(iterationId);
            String tag = "banner_" + position;
            BannerView bannerView = new BannerView(getContext(), new BannerInit() {
                @Override
                public void onInitResult(boolean initSuccess) {
                    if (!initSuccess) {
                        bannerInitFailed(false);
                    }
                }
            });
            View view = new View(getContext());
            view.setBackground(banner.bannerAppearance().backgroundDrawable());
            bannerView.setBannerBackground(banner.bannerAppearance().backgroundDrawable());
            bannerView.setSize(-1, -1, true);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (maxBannerWidth / banner.bannerAppearance().singleBannerAspectRatio()),
                    1f
            );

            if (i < columns - 1) {
                lp.setMarginEnd(Sizes.dpToPxExt(appearance.horizontalGap(), getContext()));
            }
            view.setLayoutParams(lp);
            bannerView.setLayoutParams(lp);
            bannerView.setTag(tag);
            bannerView.viewModel(
                    bannerViewModel
            );
            InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    BannerDownloadManager bannerDownloadManager = core.contentLoader().bannerDownloadManager();
                    bannerDownloadManager.setMaxPriority(banner.id(), false);
                    bannerViewModel.loadContent(false, null);
                }
            });
           // addView(view);
            addView(bannerView);
        }

    }

    private void bannerInitFailed(boolean reload) {

    }
}
