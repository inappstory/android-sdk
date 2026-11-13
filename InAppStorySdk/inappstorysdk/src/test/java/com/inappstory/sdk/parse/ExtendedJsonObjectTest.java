package com.inappstory.sdk.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ExtendedJsonObjectTest {

    // --- Тесты optStringNullable ---

    @Test
    public void optStringNullable_returnsValue_whenValidString() throws JSONException {
        JSONObject json = new JSONObject("{\"key\": \"hello\"}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertEquals("hello", extJson.optStringNullable("key"));
    }

    @Test
    public void optStringNullable_returnsNull_whenLiteralNullOrJsonNullOrMissing() throws JSONException {
        JSONObject json = new JSONObject("{\"strNull\": \" null \", \"jsonNull\": null}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertNull(extJson.optStringNullable("strNull"));
        assertNull(extJson.optStringNullable("jsonNull"));
        assertNull(extJson.optStringNullable("missingKey"));
    }

    // --- Тесты optIntNullable ---

    @Test
    public void optIntNullable_returnsValue_whenValidInt() throws JSONException {
        JSONObject json = new JSONObject("{\"count\": 42}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertEquals(Integer.valueOf(42), extJson.optIntNullable("count"));
    }

    @Test
    public void optIntNullable_returnsNull_whenNullOrInvalidType() throws JSONException {
        JSONObject json = new JSONObject("{\"strNull\": \"null\", \"jsonNull\": null, \"wrongType\": \"abc\"}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertNull(extJson.optIntNullable("strNull"));
        assertNull(extJson.optIntNullable("jsonNull"));
        assertNull(extJson.optIntNullable("wrongType"));
        assertNull(extJson.optIntNullable("missing"));
    }

    // --- Тесты optBooleanNullable ---

    @Test
    public void optBooleanNullable_returnsValue_whenValidBoolean() throws JSONException {
        JSONObject json = new JSONObject("{\"flag\": true}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertEquals(Boolean.TRUE, extJson.optBooleanNullable("flag"));
    }

    @Test
    public void optBooleanNullable_returnsNull_whenNullOrInvalidType() throws JSONException {
        JSONObject json = new JSONObject("{\"strNull\": \"null\", \"jsonNull\": null, \"wrongType\": 123}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertNull(extJson.optBooleanNullable("strNull"));
        assertNull(extJson.optBooleanNullable("jsonNull"));
        assertNull(extJson.optBooleanNullable("wrongType"));
        assertNull(extJson.optBooleanNullable("missing"));
    }

    // --- Тесты optLongNullable ---

    @Test
    public void optLongNullable_returnsValue_whenValidLong() throws JSONException {
        JSONObject json = new JSONObject("{\"timestamp\": 1700000000000}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertEquals(Long.valueOf(1700000000000L), extJson.optLongNullable("timestamp"));
    }

    @Test
    public void optLongNullable_returnsNull_whenNullOrMissing() throws JSONException {
        JSONObject json = new JSONObject("{\"strNull\": \"null\", \"jsonNull\": null}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertNull(extJson.optLongNullable("strNull"));
        assertNull(extJson.optLongNullable("jsonNull"));
        assertNull(extJson.optLongNullable("missing"));
    }

    // --- Тесты optDoubleNullable ---

    @Test
    public void optDoubleNullable_returnsValue_whenValidDouble() throws JSONException {
        JSONObject json = new JSONObject("{\"price\": 19.99}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertEquals(Double.valueOf(19.99), extJson.optDoubleNullable("price"));
    }

    @Test
    public void optDoubleNullable_returnsNull_whenNullOrMissing() throws JSONException {
        JSONObject json = new JSONObject("{\"strNull\": \"null\", \"jsonNull\": null}");
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertNull(extJson.optDoubleNullable("strNull"));
        assertNull(extJson.optDoubleNullable("jsonNull"));
        assertNull(extJson.optDoubleNullable("missing"));
    }

    // --- Тесты делегирующих opt* методов ---

    @Test
    public void standardOptMethods_delegateToUnderlyingJsonObject() throws JSONException {
        JSONObject json = new JSONObject(
                "{\"str\": \"val\", \"int\": 10, \"bool\": true, \"long\": 100, \"double\": 2.5}"
        );
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertEquals("val", extJson.optString("str"));
        assertEquals("", extJson.optString("missing"));

        assertEquals(10, extJson.optInt("int"));
        assertEquals(0, extJson.optInt("missing"));

        assertTrue(extJson.optBoolean("bool"));
        assertFalse(extJson.optBoolean("missing"));
        assertTrue(extJson.optBoolean("missing", true));

        assertEquals(100L, extJson.optLong("long"));
        assertEquals(2.5, extJson.optDouble("double"), 0.001);
    }

    // --- Тесты get* методов и throw JSONException ---

    @Test
    public void getMethods_returnValues_whenKeysExist() throws JSONException {
        JSONObject json = new JSONObject(
                "{\"str\": \"val\", \"int\": 5, \"bool\": false, \"long\": 50, \"double\": 1.5, \"obj\": {\"a\": 1}, \"arr\": [10, 20]}"
        );
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertEquals("val", extJson.getString("str"));
        assertEquals(Integer.valueOf(5), extJson.getInt("int"));
        assertEquals(Boolean.FALSE, extJson.getBoolean("bool"));
        assertEquals(50L, extJson.getLong("long"));
        assertEquals(1.5, extJson.getDouble("double"), 0.001);
        assertEquals(1, extJson.getJSONObject("obj").getInt("a"));
        assertEquals(2, extJson.getJSONArray("arr").length());
    }

    @Test(expected = JSONException.class)
    public void getString_throwsJsonException_whenKeyMissing() throws JSONException {
        ExtendedJsonObject extJson = new ExtendedJsonObject(new JSONObject());
        extJson.getString("missing");
    }

    @Test(expected = JSONException.class)
    public void getInt_throwsJsonException_whenKeyMissing() throws JSONException {
        ExtendedJsonObject extJson = new ExtendedJsonObject(new JSONObject());
        extJson.getInt("missing");
    }

    @Test(expected = JSONException.class)
    public void getBoolean_throwsJsonException_whenKeyMissing() throws JSONException {
        ExtendedJsonObject extJson = new ExtendedJsonObject(new JSONObject());
        extJson.getBoolean("missing");
    }

    @Test(expected = JSONException.class)
    public void getLong_throwsJsonException_whenKeyMissing() throws JSONException {
        ExtendedJsonObject extJson = new ExtendedJsonObject(new JSONObject());
        extJson.getLong("missing");
    }

    @Test(expected = JSONException.class)
    public void getDouble_throwsJsonException_whenKeyMissing() throws JSONException {
        ExtendedJsonObject extJson = new ExtendedJsonObject(new JSONObject());
        extJson.getDouble("missing");
    }

    @Test(expected = JSONException.class)
    public void getJSONObject_throwsJsonException_whenKeyMissing() throws JSONException {
        ExtendedJsonObject extJson = new ExtendedJsonObject(new JSONObject());
        extJson.getJSONObject("missing");
    }

    @Test(expected = JSONException.class)
    public void getJSONArray_throwsJsonException_whenKeyMissing() throws JSONException {
        ExtendedJsonObject extJson = new ExtendedJsonObject(new JSONObject());
        extJson.getJSONArray("missing");
    }

    // --- Тесты optJSONObject и optJSONArray ---

    @Test
    public void optJsonObjectAndArray_returnObjectsOrNullCorrectly() throws JSONException {
        JSONObject json = new JSONObject(
                "{\"obj\": {\"k\": \"v\"}, \"arr\": [10, 20], \"strNull\": \"null\", \"jsonNull\": null, \"wrongType\": 123}"
        );
        ExtendedJsonObject extJson = new ExtendedJsonObject(json);

        assertNotNull(extJson.optJSONObject("obj"));
        assertEquals("v", extJson.optJSONObject("obj").getString("k"));
        assertNull(extJson.optJSONObject("strNull"));
        assertNull(extJson.optJSONObject("jsonNull"));
        assertNull(extJson.optJSONObject("wrongType"));
        assertNull(extJson.optJSONObject("missing"));

        assertNotNull(extJson.optJSONArray("arr"));
        assertEquals(10, extJson.optJSONArray("arr").getInt(0));
        assertNull(extJson.optJSONArray("strNull"));
        assertNull(extJson.optJSONArray("jsonNull"));
        assertNull(extJson.optJSONArray("wrongType"));
        assertNull(extJson.optJSONArray("missing"));
    }

    @Test
    public void cleanJson_removesLiteralNullEntriesRecursively() throws JSONException {
        JSONObject nested = new JSONObject("{\"valid\": \"ok\", \"nullStr\": \" null \"}");
        JSONArray nestedNestedArray = new JSONArray("[\"a\", \"null\"]");
        JSONArray array = new JSONArray();
        array.put("item1");
        array.put("null");
        array.put(new JSONObject("{\"nestedArrNull\": \"null\", \"keep\": 1}"));
        array.put(nestedNestedArray);

        JSONObject root = new JSONObject();
        root.put("topNull", "null");
        root.put("topValid", "text");
        root.put("nestedObj", nested);
        root.put("nestedArr", array);

        ExtendedJsonObject extJson = new ExtendedJsonObject(root);
        extJson.cleanJson(root);
        assertFalse(root.has("topNull"));
        assertEquals("text", root.getString("topValid"));

        assertFalse(nested.has("nullStr"));
        assertEquals("ok", nested.getString("valid"));
        assertEquals("item1", array.getString(0));
        assertTrue(array.isNull(1));

        JSONObject objInsideArray = array.getJSONObject(2);
        assertFalse(objInsideArray.has("nestedArrNull"));
        assertEquals(1, objInsideArray.getInt("keep"));
        assertEquals("a", nestedNestedArray.getString(0));
        assertTrue(nestedNestedArray.isNull(1));
    }

    @Test
    public void cleanJsonArray_replacesLiteralNullsRecursively() throws JSONException {
        JSONArray innerArray = new JSONArray();
        innerArray.put("validInner");
        innerArray.put(" null ");

        JSONObject innerObject = new JSONObject();
        innerObject.put("keep", 42);
        innerObject.put("remove", "null");

        JSONArray rootArray = new JSONArray();
        rootArray.put("validRoot");
        rootArray.put("null");
        rootArray.put(innerArray);
        rootArray.put(innerObject);

        ExtendedJsonObject extJson = new ExtendedJsonObject(new JSONObject());
        extJson.cleanJsonArray(rootArray);

        // Проверяем корневой массив
        assertEquals("validRoot", rootArray.getString(0));
        assertTrue(rootArray.isNull(1));

        // Проверяем вложенный массив
        assertEquals("validInner", innerArray.getString(0));
        assertTrue(innerArray.isNull(1));

        // Проверяем вложенный объект
        assertEquals(42, innerObject.getInt("keep"));
        assertFalse(innerObject.has("remove"));
    }
}
