package com.inappstory.sdk.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayUtil {
    public static int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        int i = 0;
        Integer temp;
        for (Iterator<Integer> it = list.iterator();
             it.hasNext();
             ret[i++] = ((temp = it.next()) != null) ? temp : 0)
            ;
        return ret;
    }

    public static List<Integer> toIntegerList(int[] list) {
        List<Integer> result = new ArrayList<Integer>(list.length);
        for (int i : list) {
            result.add(i);
        }
        return result;
    }

    public static List<Long> toLongList(long[] list) {
        List<Long> result = new ArrayList<Long>(list.length);
        for (long i : list) {
            result.add(i);
        }
        return result;
    }

    public static long[] toLongArray(List<Long> list) {
        long[] ret = new long[list.size()];
        int i = 0;
        Long temp;
        for (Iterator<Long> it = list.iterator();
             it.hasNext();
             ret[i++] = ((temp = it.next()) != null) ? temp : 0L)
            ;
        return ret;
    }
}
