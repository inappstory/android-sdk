package com.inappstory.sdk.utils.format;

public class NumberUtils {
    public Integer convertNumberToInt(Object number) {
        if (number == null) return null;
        if (number instanceof Integer) {
            return (Integer) number;
        } else if (number instanceof Float) {
            return ((Float)number).intValue();
        } else if (number instanceof Double) {
            return ((Double)number).intValue();
        }
        return null;
    }

    public Float convertNumberToFloat(Object number) {
        if (number == null) return null;
        if (number instanceof Float) {
            return (Float) number;
        } else if (number instanceof Integer) {
            return ((Integer)number).floatValue();
        } else if (number instanceof Double) {
            return ((Double)number).floatValue();
        }
        return null;
    }

    public Double convertNumberToDouble(Object number) {
        if (number == null) return null;
        if (number instanceof Double) {
            return (Double) number;
        } else if (number instanceof Integer) {
            return ((Integer)number).doubleValue();
        } else if (number instanceof Float) {
            return ((Float)number).doubleValue();
        }
        return null;
    }
}
