package com.inappstory.sdk.stories.ui.list;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.R;
import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.stories.cache.Downloader;
import com.inappstory.sdk.stories.cache.FileLoadProgressCallback;
import com.inappstory.sdk.stories.ui.video.VideoPlayer;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoryListItem extends RecyclerView.ViewHolder {

    AppCompatTextView title;
    AppCompatTextView source;
    AppCompatImageView image;
    VideoPlayer video;
    AppCompatImageView hasAudioIcon;
    View border;
    AppearanceManager manager;
    boolean isFavorite;
    IGetFavoriteListItem getFavoriteListItem;
    IStoriesListItem getListItem;

    protected View getDefaultFavoriteCell() {
        int count = InAppStoryService.isNotNull() ?
                InAppStoryService.getInstance().getFavoriteImages().size() : 0;
        if (getFavoriteListItem != null && getFavoriteListItem.getFavoriteItem() != null) {
            return getFavoriteListItem.getFavoriteItem();
        }
        View v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_favorite, null, false);
        RoundedCornerLayout cv = v.findViewById(R.id.inner_cv);
        cv.setRadius(manager.csListItemRadius());
        cv.setBackgroundColor(Color.WHITE);
        title = v.findViewById(R.id.title);
        return v;
    }

    private void createDefaultFavoriteCell() {
    }

    private void createDefaultCell() {
    }

    protected View getDefaultCell() {

        View v;
        if (getListItem != null) {
            v = getListItem.getView();
        } else {
            v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_item, null, false);
            View container = v.findViewById(R.id.container);
            if (manager.csListItemInterface() == null || (manager.csListItemInterface().getView() == null
                    && manager.csListItemInterface().getVideoView() == null)) {
                if (manager.csListItemHeight() != null) {
                    container.getLayoutParams().height = manager.csListItemHeight();
                }
                if (manager.csListItemWidth() != null) {
                    container.getLayoutParams().width = manager.csListItemWidth();
                }
            }
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(manager.csListItemRadius());
            title = v.findViewById(R.id.title);
            source = v.findViewById(R.id.source);
            hasAudioIcon = v.findViewById(R.id.hasAudio);
            image = v.findViewById(R.id.image);
            border = v.findViewById(R.id.border);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemTitleSize());
            title.setTextColor(manager.csListItemTitleColor());
            source.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemSourceSize());
            source.setTextColor(manager.csListItemSourceColor());
            border.getBackground().setColorFilter(manager.csListItemBorderColor(),
                    PorterDuff.Mode.SRC_ATOP);
        }
        return v;
    }

    protected View getDefaultVideoCell() {
        View v;
        if (getListItem != null) {
            v = (getListItem.getVideoView() != null ? getListItem.getVideoView() : getListItem.getView());
        } else {
            v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_video_inner_item, null, false);
            if (manager.csListItemInterface() == null || (manager.csListItemInterface().getView() == null
                    && manager.csListItemInterface().getVideoView() == null)) {

                View container = v.findViewById(R.id.container);
                if (manager.csListItemHeight() != null) {
                    container.getLayoutParams().height = manager.csListItemHeight();
                }
                if (manager.csListItemWidth() != null) {
                    container.getLayoutParams().width = manager.csListItemWidth();
                }
            }
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(manager.csListItemRadius());
            title = v.findViewById(R.id.title);
            source = v.findViewById(R.id.source);
            hasAudioIcon = v.findViewById(R.id.hasAudio);
            video = v.findViewById(R.id.video);
            image = v.findViewById(R.id.image);
            border = v.findViewById(R.id.border);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemTitleSize());
            title.setTextColor(manager.csListItemTitleColor());
            source.setTextSize(TypedValue.COMPLEX_UNIT_PX, manager.csListItemSourceSize());
            source.setTextColor(manager.csListItemSourceColor());
            ((GradientDrawable) border.getBackground()).setCornerRadius((int) (1.25 * manager.csListItemRadius()));
            border.getBackground().setColorFilter(manager.csListItemBorderColor(),
                    PorterDuff.Mode.SRC_ATOP);
        }
        return v;
    }

    View v0;

    public boolean isOpened;
    public boolean hasVideo;

    public StoryListItem(@NonNull View itemView, AppearanceManager manager, boolean isOpened, boolean isFavorite, boolean hasVideo) {
        super(itemView);
        this.manager = manager;
        this.isFavorite = isFavorite;
        this.isOpened = isOpened;
        this.hasVideo = hasVideo;
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        getFavoriteListItem = manager.csFavoriteListItemInterface();
        getListItem = manager.csListItemInterface();

        if (isFavorite) {
            vg.addView(getDefaultFavoriteCell());
        } else {
            if (hasVideo) {
                v0 = getDefaultVideoCell();
                // setIsRecyclable(false);
            } else {
                v0 = getDefaultCell();
            }
            vg.addView(v0);
        }
        if (manager.csListItemMargin() >= 0) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(Sizes.dpToPxExt(manager.csListItemMargin() / 2), 0,
                    Sizes.dpToPxExt(manager.csListItemMargin() / 2), 0);
            itemView.setLayoutParams(lp);
        }

    }

    private void setImage(AppCompatImageView imageView, FavoriteImage image) {
        if (image.getImage() != null && InAppStoryService.isNotNull()) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(image.getUrl(), -1, imageView,
                    InAppStoryService.getInstance().getFastCache());
        } else {
            imageView.setBackgroundColor(image.getBackgroundColor());
        }
    }

    private void loadFavoriteImages(final LoadFavoriteImagesCallback callback, final int count) {
        final List<String> downloadImages = new ArrayList<>();
        final int[] i = {0};
        RunnableCallback runnableCallback = new RunnableCallback() {
            @Override
            public void run(String path) {
                if (InAppStoryService.isNull()) return;
                downloadImages.add(path);
                i[0]++;
                if (i[0] >= count)
                    callback.onLoad(downloadImages);
                else {
                    downloadFileAndSendToInterface(InAppStoryService.getInstance()
                            .getFavoriteImages().get(i[0]).getUrl(), this);
                }
            }

            @Override
            public void error() {
                if (InAppStoryService.isNull()) return;
                downloadImages.add(null);
                i[0]++;
                if (i[0] >= count)
                    callback.onLoad(downloadImages);
                else {
                    downloadFileAndSendToInterface(InAppStoryService.getInstance()
                            .getFavoriteImages().get(i[0]).getUrl(), this);
                }
            }
        };
        downloadFileAndSendToInterface(InAppStoryService.getInstance()
                .getFavoriteImages().get(0).getUrl(), runnableCallback);
    }

    interface LoadFavoriteImagesCallback {
        void onLoad(List<String> downloadImages);
    }

    public void bindFavorite() {

        if (getFavoriteListItem != null
                && InAppStoryService.isNotNull()
                && getFavoriteListItem.getFavoriteItem() != null) {
            int count = Math.min(InAppStoryService.getInstance().getFavoriteImages().size(), 4);
            final List<Integer> backgroundColors = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                backgroundColors.add(InAppStoryService.getInstance().getFavoriteImages().get(j).getBackgroundColor());
            }
            getFavoriteListItem.bindFavoriteItem(itemView, backgroundColors, count);
            loadFavoriteImages(new LoadFavoriteImagesCallback() {
                @Override
                public void onLoad(List<String> downloadImages) {
                    getFavoriteListItem.setImages(itemView, downloadImages, backgroundColors,
                            downloadImages.size());
                }
            }, count);
            return;
        }
        RelativeLayout imageViewLayout = itemView.findViewById(R.id.container);
        boolean lpC = false;
        View outerLayout = itemView.findViewById(R.id.outerLayout);
        if (manager.csListItemHeight() != null) {
            outerLayout.getLayoutParams().height = manager.csListItemHeight();
            lpC = true;
        }
        if (manager.csListItemWidth() != null) {
            outerLayout.getLayoutParams().width = manager.csListItemWidth();
            lpC = true;
        }
        if (lpC) itemView.findViewById(R.id.outerLayout).requestLayout();
        if (title != null) {
            title.setText("Favorites");
            if (manager.csCustomFont() != null) {
                title.setTypeface(manager.csCustomFont());
            }
            title.setTextColor(manager.csListItemTitleColor());
        }
        List<FavoriteImage> favImages = InAppStoryService.getInstance().getFavoriteImages();
        int halfHeight = Sizes.dpToPxExt(55);
        int halfWidth = Sizes.dpToPxExt(55);
        int height = Sizes.dpToPxExt(110);
        int width = Sizes.dpToPxExt(110);
        if (manager.csListItemInterface() == null || (manager.csListItemInterface().getView() == null
                && manager.csListItemInterface().getVideoView() == null)) {
            if (manager.csListItemHeight() != null) {
                height = manager.csListItemHeight() - Sizes.dpToPxExt(10);
                halfHeight = manager.csListItemHeight() / 2 - Sizes.dpToPxExt(5);
            }
            if (manager.csListItemWidth() != null) {
                width = manager.csListItemWidth() - Sizes.dpToPxExt(10);
                halfWidth = manager.csListItemWidth() / 2 - Sizes.dpToPxExt(5);
            }
        }
        if (favImages.size() > 0 && imageViewLayout != null) {
            AppCompatImageView image1 = new AppCompatImageView(itemView.getContext());
            AppCompatImageView image2 = new AppCompatImageView(itemView.getContext());
            AppCompatImageView image3 = new AppCompatImageView(itemView.getContext());
            AppCompatImageView image4 = new AppCompatImageView(itemView.getContext());

            RelativeLayout.LayoutParams piece2;
            RelativeLayout.LayoutParams piece3;
            RelativeLayout.LayoutParams piece4;
            switch (favImages.size()) {
                case 1:
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    setImage(image1, favImages.get(0));
                    imageViewLayout.addView(image1);
                    break;
                case 2:
                    piece2 = new RelativeLayout.LayoutParams(halfWidth,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(width - halfWidth,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    image2.setLayoutParams(piece2);

                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    break;
                case 3:
                    piece2 = new RelativeLayout.LayoutParams(halfWidth,
                            height - halfHeight);
                    piece3 = new RelativeLayout.LayoutParams(halfWidth,
                            halfHeight);
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(width - halfWidth,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    image2.setLayoutParams(piece2);
                    image3.setLayoutParams(piece3);
                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    setImage(image3, favImages.get(2));
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    imageViewLayout.addView(image3);
                    break;
                default:

                    piece2 = new RelativeLayout.LayoutParams(halfWidth,
                            height - halfHeight);
                    piece3 = new RelativeLayout.LayoutParams(width - halfWidth,
                            halfHeight);
                    piece4 = new RelativeLayout.LayoutParams(halfWidth,
                            halfHeight);

                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(width - halfWidth,
                            height - halfHeight));
                    image2.setLayoutParams(piece2);
                    image3.setLayoutParams(piece3);
                    image4.setLayoutParams(piece4);
                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    setImage(image3, favImages.get(2));
                    setImage(image4, favImages.get(3));
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    imageViewLayout.addView(image3);
                    imageViewLayout.addView(image4);
                    break;

            }
        }
    }

    interface RunnableCallback {
        void run(String path);

        void error();
    }

    private void downloadFileAndSendToInterface(String url, final RunnableCallback callback) {
        if (InAppStoryService.isNull()) return;
        Downloader.downloadFileBackground(url, InAppStoryService.getInstance().getFastCache(), new FileLoadProgressCallback() {
            @Override
            public void onProgress(int loadedSize, int totalSize) {

            }

            @Override
            public void onSuccess(File file) {
                final String path = file.getAbsolutePath();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (getListItem != null) {
                            callback.run(path);
                        }
                    }
                });
            }

            @Override
            public void onError() {

            }
        });
    }

    public Integer backgroundColor;
    public ClickCallback callback;

    public void bind(Integer id,
                     String titleText,
                     Integer titleColor,
                     String sourceText,
                     final String imageUrl,
                     Integer backgroundColor,
                     boolean isOpened,
                     boolean hasAudio,
                     String videoUrl,
                     ClickCallback callback) {
        this.callback = callback;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StoryListItem.this.callback != null)
                    StoryListItem.this.callback.onItemClick(getAbsoluteAdapterPosition());
            }
        });
        if (getListItem != null) {
            this.backgroundColor = backgroundColor;
            getListItem.setId(itemView, id);
            getListItem.setTitle(itemView, titleText, titleColor);
            getListItem.setHasAudio(itemView, hasAudio);
            final String imgUrl = imageUrl;
            if (imageUrl != null) {
                String fileLink = ImageLoader.getInstance().getFileLink(imageUrl);
                if (fileLink != null) {
                    getListItem.setImage(itemView, fileLink,
                            StoryListItem.this.backgroundColor);
                } else {
                    downloadFileAndSendToInterface(imageUrl, new RunnableCallback() {
                        @Override
                        public void run(String path) {
                            ImageLoader.getInstance().addLink(imgUrl, path);
                            getListItem.setImage(itemView, path,
                                    StoryListItem.this.backgroundColor);
                        }

                        @Override
                        public void error() {
                            getListItem.setImage(itemView, null,
                                    StoryListItem.this.backgroundColor);
                        }
                    });
                }
            } else {
                getListItem.setImage(itemView, null,
                        StoryListItem.this.backgroundColor);
            }

            getListItem.setOpened(itemView, isOpened);
            if (videoUrl != null) {
                downloadFileAndSendToInterface(videoUrl, new RunnableCallback() {
                    @Override
                    public void run(String path) {
                        getListItem.setVideo(itemView, path);
                    }

                    @Override
                    public void error() {
                        getListItem.setVideo(itemView, null);
                    }
                });
            }
            return;
        }

        RoundedCornerLayout cv = itemView.findViewById(R.id.item_cv);
        cv.setBackgroundColor(Color.TRANSPARENT);
        cv.setRadius(manager.csListItemRadius());
        if (border != null)
            ((GradientDrawable) border.getBackground()).setCornerRadius((int) (1.25 * manager.csListItemRadius()));
        if (title != null) {
            title.setText(titleText);
            if (titleColor != null) {
                title.setTextColor(titleColor);
            } else {
                title.setTextColor(manager.csListItemTitleColor());
            }
            if (manager.csCustomFont() != null) {
                title.setTypeface(manager.csCustomFont());
            }
        }
        if (source != null) {
            source.setText(sourceText);
            if (manager.csCustomFont() != null) {
                source.setTypeface(manager.csCustomFont());
            }
        }
        if (hasAudioIcon != null)
            hasAudioIcon.setVisibility(hasAudio ? View.VISIBLE : View.GONE);
        if (border != null)
            border.setVisibility(isOpened ? View.GONE : View.VISIBLE);
        if (InAppStoryService.isNull()) return;
        if (videoUrl != null) {
            if (image != null) {
                if (imageUrl != null) {
                    //  image.setImageResource(0);
                    ImageLoader.getInstance().displayImage(imageUrl, 0, image,
                            InAppStoryService.getInstance().getFastCache());
                } else if (backgroundColor != null) {
                    image.setImageResource(0);
                    image.setBackgroundColor(backgroundColor);
                }
            }
            if (video != null) {
                video.release();
                video.loadVideoByUrl(videoUrl);
            }
        } else {
            if (video != null) {
                video.release();
            }
            if (image != null) {
                if (imageUrl != null) {
                    //  image.setImageResource(0);
                    ImageLoader.getInstance().displayImage(imageUrl, 0, image,
                            InAppStoryService.getInstance().getFastCache());
                } else if (backgroundColor != null) {
                    image.setImageResource(0);
                    image.setBackgroundColor(backgroundColor);
                }
            }
        }
    }
}
