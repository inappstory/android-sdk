package com.inappstory.sdk.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated will be removed in SDK 2.0
 * Switch to InAppStoryManager and StoriesList callbacks
 * */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Deprecated
public @interface CsSubscribe {
    CsThreadMode threadMode() default CsThreadMode.ASYNC;
}

