package io.casestory.sdk.stories.ui.list;

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

import io.casestory.casestorysdk.R;
import io.casestory.sdk.AppearanceManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.imageloader.ImageLoader;
import io.casestory.sdk.imageloader.RoundedCornerLayout;
import io.casestory.sdk.stories.ui.views.IGetFavoriteListItem;
import io.casestory.sdk.stories.ui.views.IStoriesListItem;
import io.casestory.sdk.stories.utils.Sizes;

public class StoryListItem extends RecyclerView.ViewHolder {

    AppCompatTextView title;
    AppCompatTextView source;
    AppCompatImageView image;
    View border;
    AppearanceManager manager;
    boolean isFavorite;
    IGetFavoriteListItem getFavoriteListItem;
    IStoriesListItem getListItem;

    protected View getDefaultFavoriteCell() {
        if (getFavoriteListItem != null && getFavoriteListItem.getFavoriteItem(CaseStoryService.getInstance().favoriteImages) != null) {
            return getFavoriteListItem.getFavoriteItem(CaseStoryService.getInstance().favoriteImages);
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
        if (manager.csListItemMargin() > 0) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(Sizes.dpToPxExt(manager.csListItemMargin() / 2), Sizes.dpToPxExt(2),
                    Sizes.dpToPxExt(manager.csListItemMargin()), Sizes.dpToPxExt(2));
            itemView.setLayoutParams(lp);
        }

    }

    private void setImage(AppCompatImageView imageView, String url) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage(url, -1, imageView);
    }

    public void bindFavorite() {
        if (getFavoriteListItem != null && getFavoriteListItem.getFavoriteItem(CaseStoryService.getInstance().favoriteImages) != null) {
            getFavoriteListItem.bindFavoriteItem(itemView, CaseStoryService.getInstance().favoriteImages);
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
        List<FavoriteImage> favImages = CaseStoryService.getInstance().favoriteImages;
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
                    setImage(image1, favImages.get(0).getImage().get(0).getUrl());
                    imageViewLayout.addView(image1);
                    break;
                case 2:
                    piece2 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(55),
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    image2.setLayoutParams(piece2);

                    setImage(image1, favImages.get(0).getImage().get(0).getUrl());
                    setImage(image2, favImages.get(1).getImage().get(0).getUrl());
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
                    setImage(image1, favImages.get(0).getImage().get(0).getUrl());
                    setImage(image2, favImages.get(1).getImage().get(0).getUrl());
                    setImage(image3, favImages.get(2).getImage().get(0).getUrl());
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
                    setImage(image1, favImages.get(0).getImage().get(0).getUrl());
                    setImage(image2, favImages.get(1).getImage().get(0).getUrl());
                    setImage(image3, favImages.get(2).getImage().get(0).getUrl());
                    setImage(image4, favImages.get(3).getImage().get(0).getUrl());
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    imageViewLayout.addView(image3);
                    imageViewLayout.addView(image4);
                    break;

            }
        }
    }

    public void bind(String titleText, Integer titleColor, String sourceText, String imageUrl, Integer backgroundColor, boolean isOpened) {
        if (getListItem != null) {
            getListItem.setTitle(itemView, titleText, titleColor);
            getListItem.setSource(itemView, sourceText);
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

        border.setVisibility(isOpened ? View.GONE : View.VISIBLE);
        if (image != null) {
            if (imageUrl != null) {

                ImageLoader.getInstance().displayImage(imageUrl, 0, image);

            } else if (backgroundColor != null) {
                image.setBackgroundColor(backgroundColor);
            }
        }
    }
}
