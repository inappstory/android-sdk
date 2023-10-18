package com.inappstory.sdk.core.network.annotations.api;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Field {
  String value();
  boolean encoded() default false;
}