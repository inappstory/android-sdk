package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.ShareObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToShareObjectTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedShareObject_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ShareObject.url, "https://example.com/share");
        json.put(ModelFields.ShareObject.title, "Share Title");
        json.put(ModelFields.ShareObject.description, "Share Description");

        ToShareObject parser = new ToShareObject(json.toString());
        ShareObject result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/share", result.url);
        assertEquals("Share Title", result.title);
        assertEquals("Share Description", result.description);
    }

    @Test
    public void parse_returnsShareObjectWithNullOptionalFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ShareObject.url, "https://example.com/share");
        // title и description опущены

        ToShareObject parser = new ToShareObject(json.toString());
        ShareObject result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/share", result.url);
        assertNull(result.title);
        assertNull(result.description);
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredUrlIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ShareObject.title, "No URL");

        // getString выбросит исключение -> catch вернет null
        assertNull(new ToShareObject(json.toString()).parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredUrlHasInvalidDataType() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ShareObject.url, 12345); // Ожидается строка

        assertNull(new ToShareObject(json.toString()).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToShareObject((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToShareObject("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToShareObject("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.ShareObject.url, "https://example.com/share");
        // Передаем невалидные типы для опциональных полей
        json.put(ModelFields.ShareObject.title, 123);
        json.put(ModelFields.ShareObject.description, new JSONObject());

        ToShareObject parser = new ToShareObject(json.toString());
        ShareObject result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/share", result.url);
        assertNull(result.title);
        assertNull(result.description);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToShareObject parser = new ToShareObject("{}");
        assertEquals("ShareObject", parser.getModelClassName());
    }
}