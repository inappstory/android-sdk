package com.inappstory.sdk.stories.ui.views.goodswidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.game.cache.SuccessUseCaseCallback;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.imageloader.RoundedCornerLayout;
import com.inappstory.sdk.memcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.stories.cache.usecases.CustomFileUseCase;
import com.inappstory.sdk.stories.utils.Sizes;

import java.io.File;

public class SimpleCustomGoodsItem implements ICustomGoodsItem {
    public SimpleCustomGoodsItem csGoodsCellImageBackgroundColor(int goodsCellImageBackgroundColor) {
        this.goodsCellImageBackgroundColor = goodsCellImageBackgroundColor;
        return this;
    }

    public SimpleCustomGoodsItem csGoodsCellImageCornerRadius(int goodsCellImageCornerRadius) {
        this.goodsCellImageCornerRadius = goodsCellImageCornerRadius;
        return this;
    }

    public SimpleCustomGoodsItem csGoodsCellMainTextColor(int goodsCellMainTextColor) {
        this.goodsCellMainTextColor = goodsCellMainTextColor;
        return this;
    }

    public SimpleCustomGoodsItem csGoodsCellOldPriceTextColor(int goodsCellOldPriceTextColor) {
        this.goodsCellOldPriceTextColor = goodsCellOldPriceTextColor;
        return this;
    }

    public SimpleCustomGoodsItem csGoodsCellTitleSize(int goodsCellTitleSize) {
        this.goodsCellTitleSize = goodsCellTitleSize;
        return this;
    }

    public SimpleCustomGoodsItem csGoodsCellDescriptionSize(int goodsCellDescriptionSize) {
        this.goodsCellDescriptionSize = goodsCellDescriptionSize;
        return this;
    }

    public SimpleCustomGoodsItem csGoodsCellPriceSize(int goodsCellPriceSize) {
        this.goodsCellPriceSize = goodsCellPriceSize;
        return this;
    }

    public SimpleCustomGoodsItem csGoodsCellOldPriceSize(int goodsCellOldPriceSize) {
        this.goodsCellOldPriceSize = goodsCellOldPriceSize;
        return this;
    }

    private int goodsCellImageBackgroundColor = Color.TRANSPARENT;
    private Integer goodsCellImageCornerRadius;
    private int goodsCellMainTextColor = Color.BLACK;
    private int goodsCellOldPriceTextColor = Color.parseColor("#CCCCCC");
    private Integer goodsCellTitleSize = null;
    private Integer goodsCellDescriptionSize = null;
    private Integer goodsCellPriceSize = null;
    private Integer goodsCellOldPriceSize = null;


    @NonNull
    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.cs_goods_default_item,
                null, false);

        ((RoundedCornerLayout) view.findViewById(R.id.item_cv))
                .setRadius(
                        goodsCellImageCornerRadius != null ?
                                goodsCellImageCornerRadius :
                                Sizes.dpToPxExt(8, context)
                );

        setTextFieldParameters(
                R.id.title,
                goodsCellMainTextColor,
                goodsCellTitleSize,
                Sizes.dpToPxExt(14, context),
                view
        );
        setTextFieldParameters(
                R.id.description,
                goodsCellMainTextColor,
                goodsCellDescriptionSize,
                Sizes.dpToPxExt(12, context),
                view
        );
        setTextFieldParameters(
                R.id.price,
                goodsCellMainTextColor,
                goodsCellPriceSize,
                Sizes.dpToPxExt(14, context),
                view
        );
        setTextFieldParameters(
                R.id.oldPrice,
                goodsCellOldPriceTextColor,
                goodsCellOldPriceSize,
                Sizes.dpToPxExt(14, context),
                view
        );

        return view;
    }

    private void setTextFieldParameters(@IdRes int id, int color, Integer size, int defaultSize, View parent) {
        AppCompatTextView tv = parent.findViewById(id);
        tv.setTextColor(color);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size != null ? size : defaultSize);
    }

    @Override
    public void bindView(final View view, final GoodsItemData data) {
        if (data.description != null) {
            AppCompatTextView desc = view.findViewById(R.id.description);
            desc.setText(data.description);
            setTypeface(desc, false, false, false);
        }
        if (data.title != null) {
            AppCompatTextView title = view.findViewById(R.id.title);
            title.setText(data.title);
            setTypeface(title, true, false, false);
        }
        if (data.price != null) {
            AppCompatTextView price = view.findViewById(R.id.price);
            price.setText(data.price);
            setTypeface(price, true, false, false);
        }
        if (data.oldPrice != null) {
            AppCompatTextView oldPrice = view.findViewById(R.id.oldPrice);
            oldPrice.setText(data.oldPrice);
            setTypeface(oldPrice, true, false, false);
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        ((AppCompatImageView) view.findViewById(R.id.image))
                .setBackgroundColor(goodsCellImageBackgroundColor);
        ((AppCompatImageView) view.findViewById(R.id.image))
                .setImageBitmap(null);
        if (data.image != null && URLUtil.isNetworkUrl(data.image)) {
            new ImageLoader().getBitmapFromUrl(data.image, new IGetBitmapFromMemoryCache() {
                @Override
                public void get(Bitmap bitmap) {
                    ((AppCompatImageView) view.findViewById(R.id.image)).setImageBitmap(bitmap);
                }
            });
        }
    }

    private void setTypeface(AppCompatTextView textView, boolean bold, boolean italic, boolean secondary) {
        Typeface t = AppearanceManager.getCommonInstance().getFont(secondary, bold, italic);
        int boldV = bold ? 1 : 0;
        int italicV = italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), boldV + italicV);
    }
}
