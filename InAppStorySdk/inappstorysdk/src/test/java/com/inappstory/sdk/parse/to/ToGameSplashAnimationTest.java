package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameSplashAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameSplashAnimationTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedAnimation_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSplashAnimation.url, "https://example.com");
        json.put(ModelFields.GameSplashAnimation.sha1, "1234567890abcdef");
        json.put(ModelFields.GameSplashAnimation.size, 65536L);
        json.put(ModelFields.GameSplashAnimation.durationMinimalMs, 1500L);

        GameSplashAnimation result = new ToGameSplashAnimation(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertEquals("1234567890abcdef", result.sha1);
        assertEquals(Long.valueOf(65536L), result.size);
        assertEquals(Long.valueOf(1500L), result.durationMinimalMs);
    }

    @Test
    public void parse_returnsAnimationWithNullOptionalFields_whenOnlyRequiredUrlProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSplashAnimation.url, "https://example.com");

        GameSplashAnimation result = new ToGameSplashAnimation(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertNull(result.sha1);
        assertNull(result.size);
        assertNull(result.durationMinimalMs);
    }

    // --- Обработка null и отсутствия обязательного url ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToGameSplashAnimation(null).parse());
        assertNull(new ToGameSplashAnimation(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredUrlIsMissing() {
        assertNull(new ToGameSplashAnimation(new JSONObject()).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenRequiredUrlHasInvalidDataType() throws JSONException {
        JSONObject intUrl = new JSONObject().put(ModelFields.GameSplashAnimation.url, 123);
        JSONObject objUrl = new JSONObject().put(ModelFields.GameSplashAnimation.url, new JSONObject());
        JSONObject arrUrl = new JSONObject().put(ModelFields.GameSplashAnimation.url, new JSONArray());
        JSONObject nullUrl = new JSONObject().put(ModelFields.GameSplashAnimation.url, JSONObject.NULL);

        assertNull(new ToGameSplashAnimation(intUrl).parse());
        assertNull(new ToGameSplashAnimation(objUrl).parse());
        assertNull(new ToGameSplashAnimation(arrUrl).parse());
        assertNull(new ToGameSplashAnimation(nullUrl).parse());
    }

    @Test
    public void parse_setsOptionalFieldsToNull_whenOptionalFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameSplashAnimation.url, "https://example.com");
        json.put(ModelFields.GameSplashAnimation.sha1, 999);
        json.put(ModelFields.GameSplashAnimation.size, "not_a_long");
        json.put(ModelFields.GameSplashAnimation.durationMinimalMs, new JSONObject());

        GameSplashAnimation result = new ToGameSplashAnimation(json).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.url);
        assertNull(result.sha1);
        assertNull(result.size);
        assertNull(result.durationMinimalMs);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameSplashAnimation parser = new ToGameSplashAnimation(new JSONObject());

        assertEquals("GameSplashAnimation", parser.getModelClassName());
    }
}
