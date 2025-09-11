package com.inappstory.sdk.network;

import android.util.Pair;


import com.inappstory.sdk.network.annotations.models.Ignore;
import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.utils.StringsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class JsonParser {
    private static <T> T fromJson(JSONObject jsonObject, Class<T> typeOfT) throws Exception {
        T res = typeOfT.newInstance();
        for (Field field : typeOfT.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            if (field.getAnnotation(Ignore.class) != null)
                continue;
            String name = field.getName();
            if (field.getAnnotation(SerializedName.class) != null) {
                name = field.getAnnotation(SerializedName.class).value();
            }
            if (!jsonObject.has(name) || jsonObject.get(name) == null || jsonObject.get(name).toString().equals("null")) {
                if (field.getAnnotation(Required.class) != null) {
                    throw new JSONException("Required field " + field.getName() + " is empty or in wrong format");
                }
                continue;
            }
            if (field.getType().equals(Integer.TYPE) || field.getType().equals(Integer.class)) {
                field.set(res, new Integer(jsonObject.getInt(name)));
            } else if (field.getType().equals(Long.TYPE) || field.getType().equals(Long.class)) {
                field.set(res, new Long(jsonObject.getLong(name)));
            } else if (field.getType().equals(Character.TYPE) || field.getType().equals(Character.class)) {
                field.set(res, new Character((char) jsonObject.getInt(name)));
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
            } else if (field.getType().equals(List.class) || field.getType().equals(ArrayList.class)) {
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
            } else if (field.getType().equals(Map.class) ||
                    containsInterface(field.getType().getInterfaces(), Map.class)) {
                field.set(res, toObjectMap(jsonObject.getJSONObject(name)));
            } else {
                field.set(res, fromJson(jsonObject.getJSONObject(name), (Class) field.getGenericType()));
            }
        }
        return res;
    }

    public static List<Pair<String, String>> toQueryParams(String mainName, String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return mapToQueryParams(mainName, toObjectMap(jsonObject));
        } catch (Exception e) {
            return null;
        }
    }

    public static String mapToJsonString(Map<String, Object> map) {
        return new JSONObject(map).toString();
    }

    public static Map<String, Object> stringMapToEscapedObjMap(Map<String, String> map) {
        Map<String, Object> objMap = new HashMap<>();
        if (map != null) {
            for (String key : map.keySet()) {
                String val = map.get(key);
                if (key != null && val != null) {
                    objMap.put(
                            StringsUtils.getEscapedString(
                                    StringsUtils.escapeSingleQuotes(
                                            key
                                    )
                            ),
                            StringsUtils.getEscapedString(
                                    StringsUtils.escapeSingleQuotes(
                                            val
                                    )
                            )
                    );
                }
            }
        }
        return objMap;
    }

    public static String stringMapToEscapedJsonString(Map<String, String> map) {
        return new JSONObject(stringMapToEscapedObjMap(map)).toString();
    }

    private static List<Pair<String, String>> mapToQueryParams(String mainName, Map<String, Object> map) {
        ArrayList<Pair<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String newMainName = entry.getKey();
            if (mainName != null) {
                newMainName = mainName + "[" + entry.getKey() + "]";
            }
            if (entry.getValue() instanceof List) {
                result.addAll(listToQueryParams(newMainName, (List<Object>) entry.getValue()));
            } else if (entry.getValue() instanceof Map) {
                result.addAll(mapToQueryParams(newMainName, (Map<String, Object>) entry.getValue()));
            } else {
                result.add(new Pair(newMainName, entry.getValue().toString()));
            }
        }
        return result;
    }

    private static List<Pair<String, String>> listToQueryParams(String mainName, List<Object> map) {
        List<Pair<String, String>> result = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            Object entry = map.get(i);
            if (entry instanceof List) {
                result.addAll(listToQueryParams(mainName + "[" + i + "]", (List<Object>) entry));
            } else if (entry instanceof Map) {
                result.addAll(mapToQueryParams(mainName + "[" + i + "]", (Map<String, Object>) entry));
            } else {
                result.add(new Pair(mainName + "[]", entry.toString()));
            }
        }
        return result;
    }

    public static Map<String, String> toMap(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return toMap(jsonObject);
        } catch (Exception e) {
            return null;
        }
    }

    static Map<String, String> toMap(JSONObject jsonobj) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();
        Iterator<String> keys = jsonobj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonobj.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value.toString());
        }
        return map;
    }

    public static Map<String, Object> toObjectMap(JSONObject jsonobj) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonobj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonobj.get(key);
            if (value instanceof JSONArray) {
                value = toObjectList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toObjectMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    static List<Object> toObjectList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toObjectList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toObjectMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static <T> ArrayList<T> listFromJson(String json, Class<T> typeOfT) {
        ArrayList<T> res = new ArrayList<>();
        if (json == null) return res;
        try {
            JSONTokener jsonT = new JSONTokener(json);
            Object object = jsonT.nextValue();
            if (object instanceof JSONArray) {
                for (int i = 0; i < ((JSONArray) object).length(); i++) {
                    Object obj = ((JSONArray) object).get(i);
                    if (typeOfT.isPrimitive() || typeOfT.equals(Integer.class)
                            || typeOfT.equals(Boolean.class) || typeOfT.equals(Character.class)
                            || typeOfT.equals(Short.class) || typeOfT.equals(Long.class)
                            || typeOfT.equals(Byte.class) || typeOfT.equals(Float.class)
                            || typeOfT.equals(Double.class) || typeOfT.equals(String.class)) {
                        res.add((T) obj);
                    } else {
                        res.add(fromJson((JSONObject) obj, typeOfT));
                    }
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
        if (json == null) return null;
        if (typeOfT == null) return null;
        T res = null;
        try {
            JSONTokener jsonT = new JSONTokener(json);
            Object object = jsonT.nextValue();
            if (object != null && object instanceof JSONArray) {
                throw new Exception();
            } else if (object != null && object instanceof JSONObject) {
                JSONObject jsonObject = new JSONObject(json);
                res = fromJson(jsonObject, typeOfT);
            } else if (typeOfT.isPrimitive() || typeOfT.equals(Integer.class)
                    || typeOfT.equals(Boolean.class) || typeOfT.equals(Character.class)
                    || typeOfT.equals(Short.class) || typeOfT.equals(Long.class)
                    || typeOfT.equals(Byte.class) || typeOfT.equals(Float.class)
                    || typeOfT.equals(Double.class) || typeOfT.equals(String.class)) {
                return (T) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getJson(Object instance) throws Exception {
        if (instance == null) return null;
        if (instance instanceof List || instance instanceof ArrayList) {
            JSONArray arr = new JSONArray();
            for (int i = 0; i < ((List) instance).size(); i++) {
                arr.put(getJsonObject(((List) instance).get(i)));
            }
            return arr.toString();
        } else {
            return ((JSONObject) getJsonObject(instance)).toString();
        }
    }

    private static Object getJsonObject(Object instance) throws Exception {
        if (instance == null) return null;
        Class etype = instance.getClass();
        if (etype.isEnum()) {
            return ((Enum) instance).name();
        } else if (etype.isPrimitive() || etype.equals(Integer.class)
                || etype.equals(Boolean.class) || etype.equals(Character.class)
                || etype.equals(Short.class) || etype.equals(Long.class)
                || etype.equals(Byte.class) || etype.equals(Float.class)
                || etype.equals(Double.class) || etype.equals(String.class)) {
            return instance;
        }
        JSONObject object = new JSONObject();
        if (instance instanceof Map) {
            for (Object key : ((Map) instance).keySet()) {
                object.put(key.toString(), getJsonObject(((Map) instance).get(key)));
            }
            return object;
        }
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            String name = field.getName();
            if (field.getAnnotation(Ignore.class) != null) {
                continue;
            }
            if (field.getAnnotation(SerializedName.class) != null) {
                name = field.getAnnotation(SerializedName.class).value();
            }
            Object val = field.get(instance);
            if (val == null) {
                if (field.getAnnotation(Required.class) != null) {
                    throw new JSONException("Required field " + field.getName() + " is empty or in wrong format");
                }
                continue;
            }
            Class ptype = field.getType();
            Object result = null;
            if (ptype.isPrimitive() || ptype.equals(Integer.class)
                    || ptype.equals(Boolean.class) || ptype.equals(Character.class)
                    || ptype.equals(Short.class) || ptype.equals(Long.class)
                    || ptype.equals(Byte.class) || ptype.equals(Float.class)
                    || ptype.equals(Double.class) || ptype.equals(String.class)) {
                result = val;
            } else if (field.getType().equals(List.class) ||
                    containsInterface(field.getType().getInterfaces(), List.class)) {
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
                result = array;
            } else if (field.getType().equals(Map.class) ||
                    containsInterface(field.getType().getInterfaces(), Map.class)) {
                JSONObject mapObject = new JSONObject((Map<String, Object>) val);
                Map<String, Object> valMap = (Map<String, Object>) val;
                for (Map.Entry<String, Object> entry : valMap.entrySet()) {
                    Object entryVal = entry.getValue();
                    if (entryVal instanceof List) {
                        JSONArray arr = new JSONArray();
                        for (int i = 0; i < ((List) entryVal).size(); i++) {
                            arr.put(getJsonObject(((List) entryVal).get(i)));
                        }
                        mapObject.put(entry.getKey(), arr);
                    } else {
                        mapObject.put(entry.getKey(), getJsonObject(entry.getValue()));
                    }
                }
                result = mapObject;
            } else {
                result = getJsonObject(val);
            }
            if (result == null && field.getAnnotation(Required.class) != null) {
                throw new JSONException("Required field " + field.getName() + " is empty or in wrong format");
            }
            object.put(name, result);
        }
        return object;
    }

    private static boolean containsInterface(Class[] interfaces, Class className) {
        for (Class interfaceName : interfaces) {
            if (className.equals(interfaceName)) return true;
        }
        return false;
    }

}


