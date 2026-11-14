package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.core.network.content.models.SessionEditor;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToSessionEditorTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedEditor_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.SessionEditor.url, "https://editor.example.com");
        json.put(ModelFields.SessionEditor.urlTemplate, "https://editor.example.com/{id}");
        json.put(ModelFields.SessionEditor.versionTemplate, "v1.{build}");
        
        JSONObject version = new JSONObject();
        version.put("minBuild", 10000);
        version.put("editor", "v1.0");
        json.put(ModelFields.SessionEditor.versionsMap, new JSONArray().put(version));
        
        JSONObject config = new JSONObject();
        config.put("feature_flag", true);
        json.put(ModelFields.SessionEditor.config, config);
        
        JSONObject messages = new JSONObject();
        messages.put("welcome", "Hello");
        json.put(ModelFields.SessionEditor.messages, messages);

        ToSessionEditor parser = new ToSessionEditor(json.toString());
        SessionEditor result = parser.parse();

        assertNotNull(result);
        assertEquals("https://editor.example.com", result.url);
        assertEquals("https://editor.example.com/{id}", result.urlTemplate);
        assertEquals("v1.{build}", result.versionTemplate);
        assertNotNull(result.versionsMap);
        assertEquals(1, result.versionsMap.size());
        assertNotNull(result.config);
        assertEquals(true, result.config.get("feature_flag"));
        assertNotNull(result.messages);
        assertEquals("Hello", result.messages.get("welcome"));
    }

    @Test
    public void parse_returnsEditorWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToSessionEditor parser = new ToSessionEditor(json.toString());
        SessionEditor result = parser.parse();

        assertNotNull(result);
        assertNull(result.url);
        assertNull(result.urlTemplate);
        assertNull(result.versionTemplate);
        assertNull(result.versionsMap);
        assertNull(result.config); // new HashMap<> создается всегда
        assertNull(result.messages);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        assertNull(new ToSessionEditor((String) null).parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        assertNull(new ToSessionEditor("").parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        assertNull(new ToSessionEditor("{invalid_json").parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных
        json.put(ModelFields.SessionEditor.url, 123);
        json.put(ModelFields.SessionEditor.urlTemplate, new JSONObject());
        json.put(ModelFields.SessionEditor.versionsMap, "not_an_array");
        json.put(ModelFields.SessionEditor.config, "not_an_object");
        json.put(ModelFields.SessionEditor.messages, 123);

        ToSessionEditor parser = new ToSessionEditor(json.toString());
        SessionEditor result = parser.parse();

        assertNotNull(result);
        assertNull(result.url);
        assertNull(result.urlTemplate);
        assertNull(result.versionsMap);
        assertNull(result.config);
        assertNull(result.messages);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToSessionEditor parser = new ToSessionEditor("{}");
        assertEquals("SessionEditor", parser.getModelClassName());
    }
}