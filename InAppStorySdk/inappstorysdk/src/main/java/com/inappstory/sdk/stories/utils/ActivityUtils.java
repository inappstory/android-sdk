package com.inappstory.sdk.stories.utils;

import android.app.Activity;
import android.util.Log;
import android.view.ContextThemeWrapper;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActivityUtils {
    public static Integer getThemeResId(Activity activity) {
        try {
            Class<?> clazz = ContextThemeWrapper.class;
            Method method = clazz.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(activity);
        } catch (NoSuchMethodException e) {
            Log.e("TAG", "NoSuchMethodException Failed to get theme resource ID", e);
        } catch (IllegalAccessException e) {
            Log.e("TAG", "IllegalAccessException Failed to get theme resource ID", e);
        } catch (IllegalArgumentException e) {
            Log.e("TAG", "IllegalArgumentException Failed to get theme resource ID", e);
        } catch (InvocationTargetException e) {
            Log.e("TAG", "InvocationTargetException Failed to get theme resource ID", e);
        }
        return null;
    }
}
