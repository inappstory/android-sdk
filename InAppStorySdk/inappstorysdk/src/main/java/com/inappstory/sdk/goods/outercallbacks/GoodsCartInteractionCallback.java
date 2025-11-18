package com.inappstory.sdk.goods.outercallbacks;

import com.inappstory.sdk.core.api.IASCallback;

public interface GoodsCartInteractionCallback extends IASCallback {
    void addToCart(GoodsCartData good, GoodsAddToCartProcessCallback callback);

    void navigateToCart();
}
