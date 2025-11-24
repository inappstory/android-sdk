package com.inappstory.sdk.goods.outercallbacks;

public interface ProductCartUpdatedProcessCallback {
    void onSuccess(ProductCart productCart);

    void onError(String reason);
}
