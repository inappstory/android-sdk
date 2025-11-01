package com.inappstory.sdk.banners.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.banners.ICustomBannerPlaceholder;
import com.inappstory.sdk.banners.ui.banner.BannerView;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.banners.BannerDownloadManager;
import com.inappstory.sdk.core.banners.IBannerPlaceLoadCallback;
import com.inappstory.sdk.core.banners.IBannerViewModel;
import com.inappstory.sdk.core.data.IBanner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BannerListAdapter extends RecyclerView.Adapter<BannerViewHolder> {
    private final List<IBanner> banners;
    private final IASCore core;
    private final String bannerPlace;
    private final String uniqueId;
    private final float iwRatio;
    private final float itemWidth;
    private final float bannerRadius;
    private final String iterationId;
    private final ICustomBannerPlaceholder bannerPlaceholderCreator;

    public BannerListAdapter(
            IASCore core,
            @NonNull List<IBanner> banners,
            String bannerPlace,
            String uniqueId,
            ICustomBannerPlaceholder bannerPlaceholderCreator,
            String iterationId,
            float iwRatio,
            float itemWidth,
            float bannerRadius
    ) {
        this.banners = new ArrayList<>(banners);

        this.itemWidth = itemWidth;
        this.iwRatio = iwRatio;
        this.uniqueId = uniqueId;
        this.bannerPlaceholderCreator = bannerPlaceholderCreator;
        this.iterationId = iterationId;
        this.bannerPlace = bannerPlace;
        this.core = core;
        this.bannerRadius = bannerRadius;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private final String uuid = UUID.randomUUID().toString();

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cs_banner_list_item, parent, false);
        return new BannerViewHolder(view);
    }


    @Override
    public long getItemId(int position) {
        return 100L * uuid.hashCode() + position;
    }



    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerView bannerView = holder.bannerView;
        bannerView.setLoadingPlaceholder(bannerPlaceholderCreator.onCreate(bannerView.getContext()));
        bannerView.setBannerRadius(bannerRadius);
        String tag = "banner_" + position;
        bannerView.setTag(tag);
        IBanner banner = banners.get(position % banners.size());
        bannerView.setBannerBackground(banner.bannerAppearance().backgroundDrawable());
        bannerView.setSize(itemWidth, banner.bannerAppearance().singleBannerAspectRatio());
        final int bannerId = banner.id();
        final IBannerViewModel bannerViewModel = core
                .widgetViewModels()
                .bannerPlaceViewModels()
                .get(
                        uniqueId
                )
                .getBannerViewModel(
                        bannerId,
                        position
                );
        bannerViewModel.iterationId(iterationId);
        bannerView.viewModel(
                bannerViewModel
        );
        InAppStoryManager.useCoreInSeparateThread(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                BannerDownloadManager bannerDownloadManager = core.contentLoader().bannerDownloadManager();
                bannerDownloadManager.setMaxPriority(bannerId, false);
                bannerViewModel.loadContent(false, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (banners != null) return banners.size();
        return 0;
    }
}
