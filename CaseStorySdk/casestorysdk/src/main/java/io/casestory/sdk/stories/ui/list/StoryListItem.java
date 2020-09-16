package io.casestory.sdk.stories.ui.list;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
