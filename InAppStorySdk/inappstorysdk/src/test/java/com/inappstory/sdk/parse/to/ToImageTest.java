package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToImageTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedImage_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Image.url, "https://example.com");
        json.put(ModelFields.Image.width, 1080);
        json.put(ModelFields.Image.height, 1920);
        json.put(ModelFields.Image.type, "jpg");
        json.put(ModelFields.Image.expire, 3600);

        Image result = new ToImage(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals(1080, result.width);
        assertEquals(1920, result.height);
        assertEquals("jpg", result.type);
        assertEquals(3600, result.expire);
    }

    @Test
    public void parse_returnsDefaultOptionalFields_whenOnlyRequiredUrlProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Image.url, "https://example.com");

        Image result = new ToImage(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals(0, result.width);
        assertEquals(0, result.height);
        assertNull(result.type);
        assertEquals(0, result.expire);
    }

    // --- Обработка null и отсутствия обязательного url ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToImage(null).parse());
        assertNull(new ToImage(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredUrlIsMissing() throws JSONException {
        JSONObject withoutUrl = new JSONObject()
                .put(ModelFields.Image.width, 500)
                .put(ModelFields.Image.height, 500);

        assertNull(new ToImage(new JSONObject()).parse());
        assertNull(new ToImage(withoutUrl).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenRequiredUrlHasInvalidDataType() throws JSONException {
        JSONObject intUrl = new JSONObject().put(ModelFields.Image.url, 12345);
        JSONObject boolUrl = new JSONObject().put(ModelFields.Image.url, true);
        JSONObject objUrl = new JSONObject().put(ModelFields.Image.url, new JSONObject());
        JSONObject arrUrl = new JSONObject().put(ModelFields.Image.url, new JSONArray());
        JSONObject nullUrl = new JSONObject().put(ModelFields.Image.url, JSONObject.NULL);

        assertNull(new ToImage(intUrl).parse());
        assertNull(new ToImage(boolUrl).parse());
        assertNull(new ToImage(objUrl).parse());
        assertNull(new ToImage(arrUrl).parse());
        assertNull(new ToImage(nullUrl).parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInOptionalFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Image.url, "https://example.com");
        json.put(ModelFields.Image.width, "not_an_int");
        json.put(ModelFields.Image.height, new JSONObject());
        json.put(ModelFields.Image.type, 999);
        json.put(ModelFields.Image.expire, false);

        Image result = new ToImage(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals(0, result.width);
        assertEquals(0, result.height);
        assertNull(result.type);
        assertEquals(0, result.expire);

        // Проверка строкового "null" и JSONArray для поля type
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.Image.url, "https://example.com");
        json2.put(ModelFields.Image.type, " null ");
        assertNull(new ToImage(json2).parse().type);

        JSONObject json3 = new JSONObject();
        json3.put(ModelFields.Image.url, "https://example.com");
        json3.put(ModelFields.Image.type, new JSONArray());
        assertNull(new ToImage(json3).parse().type);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToImage parser = new ToImage(new JSONObject());

        assertEquals("Image", parser.getModelClassName());
    }
}
