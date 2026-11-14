package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameSplashAnimation;

import org.json.JSONObject;
import org.junit.Test;

public class FromGameSplashAnimationTest {

    @Test
    public void parse_returnsValidJsonString_whenModelIsPopulated() throws Exception {
        GameSplashAnimation model = new GameSplashAnimation();
        model.url = "https://example.com/splash.mp4";
        model.size = 102400L;
        model.durationMinimalMs = 2000L;
        model.sha1 = "abc123def456";

        FromGameSplashAnimation parser = new FromGameSplashAnimation(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("https://example.com/splash.mp4", json.getString(ModelFields.GameSplashAnimation.url));
        assertEquals(102400L, json.getLong(ModelFields.GameSplashAnimation.size));
        assertEquals(2000L, json.getLong(ModelFields.GameSplashAnimation.durationMinimalMs));
        assertEquals("abc123def456", json.getString(ModelFields.GameSplashAnimation.sha1));
    }

    // --- Негативные сценарии ---

    @Test
    public void parse_returnsNull_whenModelIsNull() {
        FromGameSplashAnimation parser = new FromGameSplashAnimation(null);
        // Базовый класс ModelToStringParser перехватит NPE и вернет null
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesNullFields_whenModelHasNullValues() throws Exception {
        GameSplashAnimation model = new GameSplashAnimation();
        model.url = null;
        model.size = 0L; // Примитив long не может быть null
        model.durationMinimalMs = 0L;
        model.sha1 = null;

        FromGameSplashAnimation parser = new FromGameSplashAnimation(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        // Строковые поля с null не добавляются
        assertFalse(json.has(ModelFields.GameSplashAnimation.url));
        assertFalse(json.has(ModelFields.GameSplashAnimation.sha1));
        // Числовые поля добавляются с нулевыми значениями
        assertEquals(0L, json.getLong(ModelFields.GameSplashAnimation.size));
        assertEquals(0L, json.getLong(ModelFields.GameSplashAnimation.durationMinimalMs));
    }

    @Test
    public void parse_handlesEmptyStringFields() throws Exception {
        GameSplashAnimation model = new GameSplashAnimation();
        model.url = "";
        model.size = 0L;
        model.durationMinimalMs = 0L;
        model.sha1 = "";

        FromGameSplashAnimation parser = new FromGameSplashAnimation(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("", json.getString(ModelFields.GameSplashAnimation.url));
        assertEquals(0L, json.getLong(ModelFields.GameSplashAnimation.size));
        assertEquals(0L, json.getLong(ModelFields.GameSplashAnimation.durationMinimalMs));
        assertEquals("", json.getString(ModelFields.GameSplashAnimation.sha1));
    }
}