package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToBannerTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedBanner_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Banner.id, 101);
        json.put(ModelFields.Banner.hasLimit, true);
        json.put(ModelFields.Banner.frequencyLimit, 3600L);
        json.put(ModelFields.Banner.displayTo, 1700005000L);
        json.put(ModelFields.Banner.displayFrom, 1700000000L);
        json.put(ModelFields.Banner.hasSwipeUp, true);
        json.put(ModelFields.Banner.disableClose, true);
        json.put(ModelFields.Banner.slides, new JSONArray());
        json.put(ModelFields.Banner.customAttributes, new JSONObject().put("attrKey", "attrVal"));
        json.put(ModelFields.Banner.layoutTemplateVariables, new JSONObject().put("varKey", "varVal"));
        json.put(ModelFields.Banner.appearance, new JSONObject().put("bg", "#FFFFFF"));

        Banner result = new ToBanner(json).parse();

        assertNotNull(result);
        assertEquals(101, result.id);
        assertTrue(result.hasLimit);
        assertEquals(Long.valueOf(3600L), result.frequencyLimit);
        assertEquals(Long.valueOf(1700005000L), result.displayTo);
        assertEquals(Long.valueOf(1700000000L), result.displayFrom);
        assertEquals(Boolean.TRUE, result.hasSwipeUp);
        assertTrue(result.disableClose);
        assertNotNull(result.slides);
        assertNotNull(result.customAttributes);
        assertEquals("attrVal", result.customAttributes.get("attrKey"));
        assertNotNull(result.layoutTemplateVariables);
        assertEquals("varVal", result.layoutTemplateVariables.get("varKey"));
        assertNotNull(result.appearance);
        assertEquals("#FFFFFF", result.appearance.get("bg"));
    }

    @Test
    public void parse_returnsBanner_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Banner.id, 77);
        json.put(ModelFields.Banner.hasLimit, false);

        Banner result = new ToBanner(json.toString()).parse();

        assertNotNull(result);
        assertEquals(77, result.id);
        assertFalse(result.hasLimit);
    }

    @Test
    public void parse_usesDefaultAndNullValues_whenOnlyRequiredIdProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Banner.id, 1);

        Banner result = new ToBanner(json).parse();

        assertNotNull(result);
        assertEquals(1, result.id);
        assertFalse(result.hasLimit);
        assertNull(result.frequencyLimit);
        assertNull(result.displayTo);
        assertNull(result.displayFrom);
        assertNull(result.hasSwipeUp);
        assertFalse(result.disableClose);
        assertNull(result.slides);
    }

    // --- Проверка обязательного поля id и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() {
        JSONObject jsonWithoutId = new JSONObject();

        assertNull(new ToBanner(jsonWithoutId).parse());
        assertNull(new ToBanner("{}").parse());
    }

    @Test
    public void parse_returnsNull_whenIdHasInvalidDataType() throws JSONException {
        JSONObject stringId = new JSONObject();
        stringId.put(ModelFields.Banner.id, "not_a_number");

        JSONObject objectId = new JSONObject();
        objectId.put(ModelFields.Banner.id, new JSONObject().put("nested", 1));

        JSONObject nullId = new JSONObject();
        nullId.put(ModelFields.Banner.id, JSONObject.NULL);

        assertNull(new ToBanner(stringId).parse());
        assertNull(new ToBanner(objectId).parse());
        assertNull(new ToBanner(nullId).parse());
    }

    @Test
    public void parse_returnsNull_whenStringIsMalformedOrNull() {
        assertNull(new ToBanner("{invalid_json").parse());
        assertNull(new ToBanner("").parse());
        assertNull(new ToBanner((String) null).parse());
        assertNull(new ToBanner((JSONObject) null).parse());
    }

    // --- Проверка невалидных типов данных для опциональных полей ---

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Banner.id, 55);

        // Передаем невалидные типы для boolean, long, JSONArray и JSONObject полей
        json.put(ModelFields.Banner.hasLimit, "not_a_boolean");
        json.put(ModelFields.Banner.disableClose, 999);
        json.put(ModelFields.Banner.frequencyLimit, "not_a_long");
        json.put(ModelFields.Banner.displayTo, new JSONObject());
        json.put(ModelFields.Banner.displayFrom, new JSONArray());
        json.put(ModelFields.Banner.hasSwipeUp, "not_a_boolean");
        json.put(ModelFields.Banner.slides, "not_an_array");
        json.put(ModelFields.Banner.customAttributes, "not_an_object");
        json.put(ModelFields.Banner.layoutTemplateVariables, 12345);
        json.put(ModelFields.Banner.appearance, new JSONArray());

        Banner result = new ToBanner(json).parse();

        // Модель должна успешно создаться, а невалидные опциональные поля получить значения по умолчанию / null
        assertNotNull(result);
        assertEquals(55, result.id);
        assertFalse(result.hasLimit);
        assertFalse(result.disableClose);
        assertNull(result.frequencyLimit);
        assertNull(result.displayTo);
        assertNull(result.displayFrom);
        assertNull(result.hasSwipeUp);
        assertNull(result.slides);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToBanner parser = new ToBanner(new JSONObject());

        assertEquals("Banner", parser.getModelClassName());
    }
}