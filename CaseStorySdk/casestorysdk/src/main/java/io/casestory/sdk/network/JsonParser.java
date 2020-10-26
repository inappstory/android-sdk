package io.casestory.sdk.network;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;


public class JsonParser {
    private static <T> T fromJson(JSONObject jsonObject, Class<T> typeOfT) throws Exception {
        T res = typeOfT.newInstance();
        for (Field field : typeOfT.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            if (field.getAnnotation(SerializedName.class) != null) {
                name = field.getAnnotation(SerializedName.class).value();
            }
            if (!jsonObject.has(name) || jsonObject.get(name) == null || jsonObject.get(name).toString().equals("null")) continue;
            if (field.getType().equals(Integer.TYPE) || field.getType().equals(Integer.class)) {
                field.set(res, new Integer(jsonObject.getInt(name)));
            } else if (field.getType().equals(Long.TYPE) || field.getType().equals(Long.class)) {
                field.set(res, new Long(jsonObject.getLong(name)));
            } else if (field.getType().equals(Character.TYPE) || field.getType().equals(Character.class)) {
                field.set(res, new Character((char)jsonObject.getInt(name)));
            } else if (field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class)) {
                field.set(res, new Boolean(jsonObject.getBoolean(name)));
            } else if (field.getType().equals(Byte.TYPE) || field.getType().equals(Byte.class)) {
                field.set(res, new Byte((byte) jsonObject.getInt(name)));
            } else if (field.getType().equals(Short.TYPE) || field.getType().equals(Short.class)) {
                field.set(res, new Short((short) jsonObject.getInt(name)));
            } else if (field.getType().equals(Float.TYPE) || field.getType().equals(Float.class)) {
                field.set(res, new Float((float) jsonObject.getDouble(name)));
            } else if (field.getType().equals(Double.TYPE) || field.getType().equals(Double.class)) {
                field.set(res, new Double(jsonObject.getDouble(name)));
            } else if (field.getType().equals(String.class)) {
                field.set(res, jsonObject.getString(name));
            } else if (field.getType().equals(List.class)) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Class ptype;
                ArrayList<Object> arrayList = new ArrayList<>();
                if (parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
                    ptype = (Class) ((ParameterizedType) parameterizedType.getActualTypeArguments()[0]).getActualTypeArguments()[0];
                    JSONArray array = jsonObject.getJSONArray(name);

                    if (ptype.isPrimitive() || ptype.equals(Integer.class)
                            || ptype.equals(Boolean.class) || ptype.equals(Character.class)
                            || ptype.equals(Short.class) || ptype.equals(Long.class)
                            || ptype.equals(Byte.class) || ptype.equals(Float.class)
                            || ptype.equals(Double.class) || ptype.equals(String.class)) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONArray innerArray = (JSONArray) array.get(i);
                            ArrayList<Object> innerList = new ArrayList<>();
                            for (int j = 0; j < innerArray.length(); j++) {
                                innerList.add(innerArray.get(j));
                            }
                            arrayList.add(innerList);
                        }
                    } else {
                        for (int i = 0; i < array.length(); i++) {
                            JSONArray innerArray = (JSONArray) array.get(i);
                            ArrayList<Object> innerList = new ArrayList<>();
                            for (int j = 0; j < innerArray.length(); j++) {
                                innerList.add(fromJson((JSONObject) array.get(i), ptype));
                            }
                            arrayList.add(innerList);
                        }
                    }
                } else {
                    ptype = (Class) parameterizedType.getActualTypeArguments()[0];

                    JSONArray array = jsonObject.getJSONArray(name);
                    if (ptype.isPrimitive() || ptype.equals(Integer.class)
                            || ptype.equals(Boolean.class) || ptype.equals(Character.class)
                            || ptype.equals(Short.class) || ptype.equals(Long.class)
                            || ptype.equals(Byte.class) || ptype.equals(Float.class)
                            || ptype.equals(Double.class) || ptype.equals(String.class)) {
                        for (int i = 0; i < array.length(); i++) {
                            arrayList.add(array.get(i));
                        }
                    } else {
                        for (int i = 0; i < array.length(); i++) {
                            if (array.get(i) instanceof JSONObject)
                                arrayList.add(fromJson((JSONObject) array.get(i), ptype));
                        }
                    }
                }
                field.set(res, arrayList);
            } else {
                field.set(res, fromJson(jsonObject.getJSONObject(name), (Class) field.getGenericType()));
            }
        }
        return res;
    }


    public static <T> ArrayList<T> listFromJson(String json, Class<T> typeOfT) {
        ArrayList<T> res = new ArrayList<>();
        try {
            JSONTokener jsonT = new JSONTokener(json);
            Object object = jsonT.nextValue();
            if (object != null && object instanceof JSONArray) {
                for (int i = 0; i < ((JSONArray) object).length(); i++) {
                    res.add(fromJson(((JSONObject) ((JSONArray) object).get(i)), typeOfT));
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static <T> T fromJson(String json, Class<T> typeOfT) {
        T res = null;
        try {
            JSONTokener jsonT = new JSONTokener(json);
            Object object = jsonT.nextValue();
            if (object != null && object instanceof JSONArray) {
                throw new Exception();
            } else if (object != null && object instanceof JSONObject) {
                JSONObject jsonObject = new JSONObject(json);
                res = fromJson(jsonObject, typeOfT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getJson(Object instance) throws Exception {
        return ((JSONObject) getJsonObject(instance)).toString();
    }

    private static Object getJsonObject(Object instance) throws Exception {
        if (instance == null) return null;
        Class etype = instance.getClass();
        if (etype.isPrimitive() || etype.equals(Integer.class)
                || etype.equals(Boolean.class) || etype.equals(Character.class)
                || etype.equals(Short.class) || etype.equals(Long.class)
                || etype.equals(Byte.class) || etype.equals(Float.class)
                || etype.equals(Double.class) || etype.equals(String.class)) {
            return instance;
        }
        JSONObject object = new JSONObject();
        for (Field field : instance.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            if (field.getAnnotation(SerializedName.class) != null) {
                name = field.getAnnotation(SerializedName.class).value();
            }
            Object val = field.get(instance);
            if (val == null) continue;
            Class ptype = field.getType();
            if (ptype.isPrimitive() || ptype.equals(Integer.class)
                    || ptype.equals(Boolean.class) || ptype.equals(Character.class)
                    || ptype.equals(Short.class) || ptype.equals(Long.class)
                    || ptype.equals(Byte.class) || ptype.equals(Float.class)
                    || ptype.equals(Double.class) || ptype.equals(String.class)) {
                object.put(name, val);
            } else if (field.getType().equals(List.class)) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                JSONArray array = new JSONArray();
                int outerSz = ((List) val).size();
                if (parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedType) {
                    for (int i = 0; i < outerSz; i++) {
                        JSONArray innerArray = new JSONArray();
                        int innerSz = ((List) ((List) val).get(i)).size();
                        for (int j = 0; j < innerSz; j++) {
                            Object val0 = getJsonObject(((List) ((List) val).get(i)).get(j));
                            innerArray.put(val0);
                        }
                        array.put(innerArray);
                    }
                } else {
                    for (int i = 0; i < outerSz; i++) {
                        array.put(getJsonObject(((List) val).get(i)));
                    }
                }
                object.put(name, array);
            } else {
                object.put(name, getJsonObject(val));
            }

        }
        return object;
    }

}


