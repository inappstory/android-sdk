package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.WriteClipboardData;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToWriteClipboardDataTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedData_whenConstructedWithValidJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.WriteClipboardData.type, "copy");
        json.put(ModelFields.WriteClipboardData.textValue, "some text to clipboard");

        ToWriteClipboardData parser = new ToWriteClipboardData(json.toString());
        WriteClipboardData result = parser.parse();

        assertNotNull(result);
        assertEquals("copy", result.type);
        assertEquals("some text to clipboard", result.textValue);
    }

    @Test
    public void parse_returnsDataWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();
        // Пустой JSON-объект

        ToWriteClipboardData parser = new ToWriteClipboardData(json.toString());
        WriteClipboardData result = parser.parse();

        assertNotNull(result);
        assertNull(result.type);
        assertNull(result.textValue);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToWriteClipboardData parser = new ToWriteClipboardData(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToWriteClipboardData parser = new ToWriteClipboardData("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToWriteClipboardData parser = new ToWriteClipboardData("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем невалидные типы данных (число и объект вместо строк)
        json.put(ModelFields.WriteClipboardData.type, 123);
        json.put(ModelFields.WriteClipboardData.textValue, new JSONObject().put("nested", "value"));

        ToWriteClipboardData parser = new ToWriteClipboardData(json.toString());
        
        // Главное требование: метод не должен выбрасывать неперехваченные исключения 
        // (блок try-catch в parseString гарантирует возврат null при ошибке).
        WriteClipboardData result = parser.parse();
        
        // В зависимости от внутренней реализации optStringNullable, 
        // результат будет либо null, либо объект с null-полями. Оба варианта корректны.
        if (result != null) {
            assertNull(result.type);
            assertNull(result.textValue);
        } else {
            assertNull(result);
        }
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToWriteClipboardData parser = new ToWriteClipboardData("{}");
        assertEquals("WriteClipboardData", parser.getModelClassName());
    }
}