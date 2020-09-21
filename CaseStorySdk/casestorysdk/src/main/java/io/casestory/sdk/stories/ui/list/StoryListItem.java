package io.casestory.sdk.stories.ui.list;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.Map;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.AppearanceManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.stories.api.models.callbacks.GetListItem;
import io.casestory.sdk.stories.utils.Sizes;

public class StoryListItem extends RecyclerView.ViewHolder {
    public AppCompatTextView getTitle() {
        return title;
    }

    public AppCompatTextView getSource() {
        return source;
    }

    public AppCompatImageView getImage() {
        return image;
    }

    public static final String TITLE_ID = "title_id";
    public static final String SOURCE_ID = "source_id";
    public static final String IMAGE_ID = "image_id";
    public static final String BORDER_ID = "border_id";

    AppCompatTextView title;
    AppCompatTextView source;
    AppCompatImageView image;
    View border;
    GetListItem csListItemInterface;
    boolean isFavorite;

    protected View getDefaultFavoriteCell() {
        return new View(itemView.getContext());
    }

    protected View getDefaultCell(AppearanceManager manager, boolean isReaded) {

        View v = null;
        if (csListItemInterface != null) {
            Map<String, Integer> binds = csListItemInterface.getBinds();
            v = isReaded ? csListItemInterface.getReadedItem() : csListItemInterface.getItem();
            if (binds.get(TITLE_ID) != null) {
                title = v.findViewById(binds.get(TITLE_ID));
            } else {
                title = v.findViewById(R.id.title);
            }
            if (binds.get(SOURCE_ID) != null) {
                source = v.findViewById(binds.get(SOURCE_ID));
            } else {
                source = v.findViewById(R.id.source);
            }
            if (binds.get(IMAGE_ID) != null) {
                image = v.findViewById(binds.get(IMAGE_ID));
            } else {
                image = v.findViewById(R.id.image);
            }
            if (binds.get(BORDER_ID) != null) {
                border = v.findViewById(binds.get(BORDER_ID));
            } else {
                border = v.findViewById(R.id.border);
            }
        } else {
            v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.cs_story_list_inner_item, null, false);
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
            border.setVisibility(isReaded ? View.GONE : View.VISIBLE);
        }
        return v;
    }

    public StoryListItem(@NonNull View itemView, AppearanceManager manager, boolean isReaded, boolean isFavorite) {
        super(itemView);
        this.isFavorite = isFavorite;
        ViewGroup vg = itemView.findViewById(R.id.baseLayout);
        vg.removeAllViews();
        csListItemInterface = manager.csListItemInterface();
        View v = null;
        if (isFavorite) {
            v = getDefaultFavoriteCell();
        } else {
            v = getDefaultCell(manager, isReaded);
        }
        vg.addView(v);
        if (manager.csListItemMargin() > 0) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(Sizes.dpToPxExt(manager.csListItemMargin() / 2), Sizes.dpToPxExt(2),
                    Sizes.dpToPxExt(manager.csListItemMargin()), Sizes.dpToPxExt(2));
            itemView.setLayoutParams(lp);
        }

    }

    public void bindFavorite() {
      /*  RelativeLayout imageViewLayout = new RelativeLayout(itemView.getContext());
        imageViewLayout.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(120), Sizes.dpToPxExt(120)));
        if (CaseStoryService.getInstance().favoriteImages.size() > 0) {
            CoreImageView image1 = new CoreImageView(itemView.getContext());
            CoreImageView image2 = new CoreImageView(itemView.getContext());
            CoreImageView image3 = new CoreImageView(itemView.getContext());
            CoreImageView image4 = new CoreImageView(itemView.getContext());

            RelativeLayout.LayoutParams piece2;
            RelativeLayout.LayoutParams piece3;
            RelativeLayout.LayoutParams piece4;
            switch (favImages.size()) {
                case 1:
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    image1.setImageUri(favImages.get(0).getImage().get(0).getUrl(), null);
                    imageViewLayout.addView(image1);
                    break;
                case 2:
                    piece2 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    image2.setLayoutParams(piece2);
                    image1.setImageUri(favImages.get(0).getImage().get(0).getUrl(), null);
                    image2.setImageUri(favImages.get(1).getImage().get(0).getUrl(), null);
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    break;
                case 3:
                    piece2 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            Sizes.dpToPxExt(60));
                    piece3 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            Sizes.dpToPxExt(60));
                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    image2.setLayoutParams(piece2);
                    image3.setLayoutParams(piece3);
                    image1.setImageUri(favImages.get(0).getImage().get(0).getUrl(), null);
                    image2.setImageUri(favImages.get(1).getImage().get(0).getUrl(), null);
                    image3.setImageUri(favImages.get(2).getImage().get(0).getUrl(), null);
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    imageViewLayout.addView(image3);
                    break;
                default:

                    piece2 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            Sizes.dpToPxExt(60));
                    piece3 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            Sizes.dpToPxExt(60));
                    piece4 = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            Sizes.dpToPxExt(60));

                    piece2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    piece4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    image1.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(60),
                            RelativeLayout.LayoutParams.MATCH_PARENT));
                    image2.setLayoutParams(piece2);
                    image3.setLayoutParams(piece3);
                    image4.setLayoutParams(piece4);
                    image1.setImageUri(favImages.get(0).getImage().get(0).getUrl(), null);
                    image2.setImageUri(favImages.get(1).getImage().get(0).getUrl(), null);
                    image3.setImageUri(favImages.get(2).getImage().get(0).getUrl(), null);
                    image4.setImageUri(favImages.get(3).getImage().get(0).getUrl(), null);
                    imageViewLayout.addView(image1);
                    imageViewLayout.addView(image2);
                    imageViewLayout.addView(image3);
                    imageViewLayout.addView(image4);
                    break;

            }
        } else {

        }
        return imageViewLayout;*/
    }

    public void bind(String titleText, String sourceText, String imageUrl, Integer backgroundColor) {
        if (title != null) {
            title.setText(titleText);
        }
        if (source != null) {
            source.setText(sourceText);
        }
        if (image != null) {
            if (imageUrl != null) {
                RequestOptions emptyOptions = new RequestOptions().centerCrop();
                Glide.with(image).load(Uri.parse(imageUrl)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                        .skipMemoryCache(true)
                        .centerCrop()
                        .apply(emptyOptions)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
            } else if (backgroundColor != null) {
                image.setBackgroundColor(backgroundColor);
            }
        }
    }
}
