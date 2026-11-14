package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.dialogstructure.QuestionStructure;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToQuestionStructureTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedStructure_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        
        JSONObject textJson = new JSONObject();
        textJson.put("value", "What is your name?");
        json.put(ModelFields.QuestionStructure.text, textJson);
        
        JSONObject paddingJson = new JSONObject();
        paddingJson.put("top", 10);
        json.put(ModelFields.QuestionStructure.padding, paddingJson);

        ToQuestionStructure parser = new ToQuestionStructure(json);
        QuestionStructure result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.text);
        assertNotNull(result.padding);
    }

    @Test
    public void parse_returnsStructureWithNullFields_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToQuestionStructure parser = new ToQuestionStructure(json);
        QuestionStructure result = parser.parse();

        assertNotNull(result);
        assertNull(result.text);
        assertNull(result.padding);
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToQuestionStructure(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToQuestionStructure parser = new ToQuestionStructure(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesForNestedObjects_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        // Передаем строки вместо объектов
        json.put(ModelFields.QuestionStructure.text, "not_an_object");
        json.put(ModelFields.QuestionStructure.padding, 123);

        ToQuestionStructure parser = new ToQuestionStructure(json);
        QuestionStructure result = parser.parse();

        // optJSONObject вернет null для не-объектов, и вложенные парсеры корректно обработают null
        assertNotNull(result);
        assertNull(result.text);
        assertNull(result.padding);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToQuestionStructure parser = new ToQuestionStructure(new JSONObject());
        assertEquals("QuestionStructure", parser.getModelClassName());
    }
}