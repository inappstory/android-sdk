package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.WebResource;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToWebResourceTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedWebResource_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.WebResource.url, "https://example.com/resource.zip");
        json.put(ModelFields.WebResource.key, "resource_key_123");
        json.put(ModelFields.WebResource.sha1, "a1b2c3d4e5f6");
        json.put(ModelFields.WebResource.size, 1024L);

        ToWebResource parser = new ToWebResource(json);
        WebResource result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/resource.zip", result.url);
        assertEquals("resource_key_123", result.key);
        assertEquals("a1b2c3d4e5f6", result.sha1);
        assertEquals(1024L, result.size);
    }

    @Test
    public void parse_returnsWebResourceWithDefaultValues_whenOnlyRequiredFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.WebResource.url, "https://example.com/resource.zip");
        json.put(ModelFields.WebResource.key, "resource_key_123");

        ToWebResource parser = new ToWebResource(json);
        WebResource result = parser.parse();

        assertNotNull(result);
        assertEquals("https://example.com/resource.zip", result.url);
        assertEquals("resource_key_123", result.key);
        assertNull(result.sha1);
        assertEquals(0L, result.size); // optLong по умолчанию возвращает 0
    }

    // --- Проверка обязательных полей и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenRequiredUrlIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.WebResource.key, "resource_key_123");

        assertNull(new ToWebResource(json).parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredKeyIsMissing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.WebResource.url, "https://example.com/resource.zip");

        assertNull(new ToWebResource(json).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToWebResource(null).parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredFieldsHaveInvalidDataType() throws JSONException {
        JSONObject jsonWithIntUrl = new JSONObject();
        jsonWithIntUrl.put(ModelFields.WebResource.url, 123);
        jsonWithIntUrl.put(ModelFields.WebResource.key, "valid_key");

        JSONObject jsonWithObjectKey = new JSONObject();
        jsonWithObjectKey.put(ModelFields.WebResource.url, "https://example.com");
        jsonWithObjectKey.put(ModelFields.WebResource.key, new JSONObject());

        // getString для не-строки вызовет исключение, которое перехватывается в try-catch
        assertNull(new ToWebResource(jsonWithIntUrl).parse());
        assertNull(new ToWebResource(jsonWithObjectKey).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.WebResource.url, "https://example.com/resource.zip");
        json.put(ModelFields.WebResource.key, "resource_key_123");
        
        // Передаем невалидные типы для опциональных полей
        json.put(ModelFields.WebResource.sha1, 999);
        json.put(ModelFields.WebResource.size, "not_a_long");

        WebResource result = new ToWebResource(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com/resource.zip", result.url);
        assertEquals("resource_key_123", result.key);
        // optStringNullable и optLong должны корректно обработать невалидные типы (вернуть null/0)
        assertNull(result.sha1);
        assertEquals(0L, result.size);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToWebResource parser = new ToWebResource(new JSONObject());
        assertEquals("WebResource", parser.getModelClassName());
    }
}