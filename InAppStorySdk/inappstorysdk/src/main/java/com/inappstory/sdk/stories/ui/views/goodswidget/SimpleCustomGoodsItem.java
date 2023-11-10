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

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.imagememcache.GetBitmapFromCacheWithFilePath;
import com.inappstory.sdk.core.imagememcache.IGetBitmapFromMemoryCache;
import com.inappstory.sdk.core.imagememcache.IGetBitmapFromMemoryCacheError;
import com.inappstory.sdk.imageloader.ImageLoader;
import com.inappstory.sdk.stories.filedownloader.FileDownloadCallbackAdapter;
import com.inappstory.sdk.stories.filedownloader.IFileDownloadCallback;
import com.inappstory.sdk.stories.ui.views.RoundedCornerLayout;
import com.inappstory.sdk.stories.utils.Sizes;

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
    public void bindView(View view, GoodsItemData data) {
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
        final AppCompatImageView imageView = (AppCompatImageView) view.findViewById(R.id.image);
        if (data.image != null && URLUtil.isNetworkUrl(data.image)) {
            IASCore.getInstance().filesRepository.getGoodsWidgetPreview(data.image,
                    new IFileDownloadCallback() {
                        @Override
                        public void onSuccess(String fileAbsolutePath) {
                            new GetBitmapFromCacheWithFilePath(
                                    fileAbsolutePath,
                                    new IGetBitmapFromMemoryCache() {
                                        @Override
                                        public void get(Bitmap bitmap) {
                                            imageView.setImageBitmap(bitmap);
                                        }
                                    },
                                    new IGetBitmapFromMemoryCacheError() {
                                        @Override
                                        public void onError() {
                                            imageView.setBackgroundColor(goodsCellImageBackgroundColor);
                                        }
                                    }
                            ).get();
                        }

                        @Override
                        public void onError(int errorCode, String error) {
                            imageView.setBackgroundColor(goodsCellImageBackgroundColor);
                        }
                    }
            );
        } else {
            imageView.setBackgroundColor(goodsCellImageBackgroundColor);
        }
    }

    private void setTypeface(AppCompatTextView textView, boolean bold, boolean italic, boolean secondary) {
        Typeface t = AppearanceManager.getCommonInstance().getFont(secondary, bold, italic);
        int boldV = bold ? 1 : 0;
        int italicV = italic ? 2 : 0;
        textView.setTypeface(t != null ? t : textView.getTypeface(), boldV + italicV);
    }
}
