package com.inappstory.sdk.stories.ui.list;

import android.graphics.Color;
import android.graphics.PorterDuff;
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


import java.util.List;

import com.inappstory.sdk.R;
import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.stories.ui.views.IGetFavoriteListItem;
import com.inappstory.sdk.stories.ui.views.IStoriesListItem;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoryListItem extends RecyclerView.ViewHolder {

    AppCompatTextView title;
    AppCompatTextView source;
    AppCompatImageView image;
    AppCompatImageView hasAudioIcon;
    View border;
    AppearanceManager manager;
    boolean isFavorite;
    IGetFavoriteListItem getFavoriteListItem;
    IStoriesListItem getListItem;

    protected View getDefaultFavoriteCell() {
        int count = (InAppStoryService.getInstance() != null && InAppStoryService.getInstance().favoriteImages != null) ?
                InAppStoryService.getInstance().favoriteImages.size() : 0;
        if (getFavoriteListItem != null && getFavoriteListItem.getFavoriteItem(InAppStoryService.getInstance().favoriteImages, count) != null) {
            return getFavoriteListItem.getFavoriteItem(InAppStoryService.getInstance().favoriteImages, count);
        }
        View v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_favorite, null, false);
        RoundedCornerLayout cv = v.findViewById(R.id.inner_cv);
        cv.setRadius(Sizes.dpToPxExt(16));
        cv.setBackgroundColor(Color.WHITE);
        title = v.findViewById(R.id.title);
        return v;
    }

    protected View getDefaultCell() {

        View v;
        if (getListItem != null) {
            v = getListItem.getView();
        } else {
            v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_item, null, false);
            RoundedCornerLayout cv = v.findViewById(R.id.item_cv);
            cv.setBackgroundColor(Color.TRANSPARENT);
            cv.setRadius(Sizes.dpToPxExt(16));
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

    View v0;

    public boolean isOpened;

    public StoryListItem(@NonNull View itemView, AppearanceManager manager, boolean isOpened, boolean isFavorite) {
        super(itemView);
        this.manager = manager;
        this.isFavorite = isFavorite;
        this.isOpened = isOpened;
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        getFavoriteListItem = manager.csFavoriteListItemInterface();
        getListItem = manager.csListItemInterface();

        if (isFavorite) {
            vg.addView(getDefaultFavoriteCell());
        } else {
            v0 = getDefaultCell();
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
        if (image.getImage() != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage(image.getImage().get(0).getUrl(), -1, imageView);
        } else {
            imageView.setBackgroundColor(Color.parseColor(image.backgroundColor));
        }
    }

    public void bindFavorite() {
        int count = (InAppStoryService.getInstance() != null && InAppStoryService.getInstance().favoriteImages != null) ?
        InAppStoryService.getInstance().favoriteImages.size() : 0;

        if (getFavoriteListItem != null && getFavoriteListItem.getFavoriteItem(InAppStoryService.getInstance().favoriteImages, count) != null) {
            getFavoriteListItem.bindFavoriteItem(itemView, InAppStoryService.getInstance().favoriteImages, count);
            return;
        }
        RelativeLayout imageViewLayout = itemView.findViewById(R.id.container);
        boolean lpC = false;
        if (manager.csListItemHeight() != null) {
            itemView.findViewById(R.id.outerLayout).getLayoutParams().height = manager.csListItemHeight();
            lpC = true;
        }
        if (manager.csListItemWidth() != null) {
            itemView.findViewById(R.id.outerLayout).getLayoutParams().width = manager.csListItemWidth();
            lpC = true;
        }
        if (lpC) itemView.findViewById(R.id.outerLayout).requestLayout();

        title.setText("Favorites");
        if (manager.csCustomFont() != null) {
            title.setTypeface(manager.csCustomFont());
        }
        title.setTextColor(manager.csListItemTitleColor());
        List<FavoriteImage> favImages = InAppStoryService.getInstance().favoriteImages;
        if (favImages.size() > 0) {
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
                    piece2 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    image2.setLayoutParams(piece2);

                    setImage(image1, favImages.get(0));
                    setImage(image2, favImages.get(1));
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    break;
                case 3:
                    piece2 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            Sizes.dpToPxExt(55));
                    piece3 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            Sizes.dpToPxExt(55));
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
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

                    piece2 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            Sizes.dpToPxExt(55));
                    piece3 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            Sizes.dpToPxExt(55));
                    piece4 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            Sizes.dpToPxExt(55));

                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            RelativeLayout.LayoutParams.MATCH_PARENT));
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

    public void bind(String titleText,
                     Integer titleColor,
                     String sourceText,
                     String imageUrl,
                     Integer backgroundColor,
                     boolean isOpened,
                     boolean hasAudio) {
        if (getListItem != null) {
            getListItem.setTitle(itemView, titleText, titleColor);
            getListItem.setSource(itemView, sourceText);
            getListItem.setHasAudio(itemView, hasAudio);
            getListItem.setImage(itemView, imageUrl, backgroundColor);
            getListItem.setOpened(itemView, isOpened);
            return;
        }

        boolean lpC = false;
        if (manager.csListItemHeight() != null) {
            itemView.findViewById(R.id.container).getLayoutParams().height = manager.csListItemHeight();
            lpC = true;
        }
        if (manager.csListItemWidth() != null) {
            itemView.findViewById(R.id.container).getLayoutParams().width = manager.csListItemWidth();
            lpC = true;
        }
        if (lpC) itemView.findViewById(R.id.container).requestLayout();
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

        hasAudioIcon.setVisibility(hasAudio ? View.VISIBLE : View.GONE);

        border.setVisibility(isOpened ? View.GONE : View.VISIBLE);
        if (image != null) {
            if (imageUrl != null) {
              //  image.setImageResource(0);
                ImageLoader.getInstance().displayImage(imageUrl, 0, image);
            } else if (backgroundColor != null) {
                image.setImageResource(0);
                image.setBackgroundColor(backgroundColor);
            }
        }
    }
}
