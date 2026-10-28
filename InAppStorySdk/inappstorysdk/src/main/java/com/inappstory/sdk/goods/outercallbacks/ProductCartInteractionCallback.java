package com.inappstory.sdk.goods.outercallbacks;

import com.inappstory.sdk.core.api.IASCallback;

public interface ProductCartInteractionCallback extends IASCallback {
    void cartUpdate(ProductCartOffer productCartOffer, ProductCartUpdatedProcessCallback callback);

    void cartClicked();

    void cartGetState(ProductCartUpdatedProcessCallback callback);
}
