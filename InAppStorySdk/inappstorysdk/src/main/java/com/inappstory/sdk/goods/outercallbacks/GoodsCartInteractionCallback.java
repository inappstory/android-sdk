package com.inappstory.sdk.goods.outercallbacks;

import com.inappstory.sdk.core.api.IASCallback;

public interface GoodsCartInteractionCallback extends IASCallback {
    void cartUpdated(GoodsCartData good, GoodsCartUpdatedProcessCallback callback);

    void cartClicked();
}
