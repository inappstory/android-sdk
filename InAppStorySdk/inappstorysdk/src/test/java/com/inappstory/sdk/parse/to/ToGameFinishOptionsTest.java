package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameFinishOptions;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameFinishOptionsTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedFinishOptions_whenAllValidFieldsProvided() throws JSONException {
        JSONObject gameObj = new JSONObject()
                .put(ModelFields.GameFinishGameOptions.id, "game_1");

        JSONObject storyObj = new JSONObject()
                .put(ModelFields.GameFinishStoryOptions.id, "story_2")
                .put(ModelFields.GameFinishStoryOptions.slideIndex, 4);

        JSONObject json = new JSONObject();
        json.put(ModelFields.GameFinishOptions.openUrl, "https://example.com");
        json.put(ModelFields.GameFinishOptions.openGameInstance, gameObj);
        json.put(ModelFields.GameFinishOptions.openStory, storyObj);

        GameFinishOptions result = new ToGameFinishOptions(json.toString()).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.openUrl);

        assertNotNull(result.openGameInstance);
        assertEquals("game_1", result.openGameInstance.id);

        assertNotNull(result.openStory);
        assertEquals("story_2", result.openStory.id);
        assertEquals(4, result.openStory.slideIndex);
    }

    @Test
    public void parse_returnsOptionsWithNullFields_whenJsonIsEmpty() {
        GameFinishOptions result = new ToGameFinishOptions("{}").parse();

        assertNotNull(result);
        assertNull(result.openUrl);
        assertNull(result.openGameInstance);
        assertNull(result.openStory);
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToGameFinishOptions(null).parse());
        assertNull(new ToGameFinishOptions("").parse());
        assertNull(new ToGameFinishOptions("{broken_json").parse());
        assertNull(new ToGameFinishOptions("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsFieldsToNull_whenTopLevelFieldsHaveInvalidDataTypes() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameFinishOptions.openUrl, 12345);
        json.put(ModelFields.GameFinishOptions.openGameInstance, "not_an_object");
        json.put(ModelFields.GameFinishOptions.openStory, new JSONArray());

        GameFinishOptions result = new ToGameFinishOptions(json.toString()).parse();

        assertNotNull(result);
        assertNull(result.openUrl);
        assertNull(result.openGameInstance);
        assertNull(result.openStory);

        // Проверка строкового "null" для openUrl и числа для вложенных объектов
        JSONObject json2 = new JSONObject();
        json2.put(ModelFields.GameFinishOptions.openUrl, " null ");
        json2.put(ModelFields.GameFinishOptions.openGameInstance, 99);
        json2.put(ModelFields.GameFinishOptions.openStory, true);

        GameFinishOptions result2 = new ToGameFinishOptions(json2.toString()).parse();

        assertNotNull(result2);
        assertNull(result2.openUrl);
        assertNull(result2.openGameInstance);
        assertNull(result2.openStory);
    }

    @Test
    public void parse_setsNestedObjectsToNull_whenNestedObjectsMissRequiredIdOrHaveInvalidIdType() throws JSONException {
        // Вложенные объекты переданы как JSONObject, но внутри нет обязательного id или его тип невалиден
        JSONObject badGameObj = new JSONObject()
                .put(ModelFields.GameFinishGameOptions.id, 777);

        JSONObject badStoryObj = new JSONObject()
                .put(ModelFields.GameFinishStoryOptions.slideIndex, 2);

        JSONObject json = new JSONObject();
        json.put(ModelFields.GameFinishOptions.openUrl, "https://example.com");
        json.put(ModelFields.GameFinishOptions.openGameInstance, badGameObj);
        json.put(ModelFields.GameFinishOptions.openStory, badStoryObj);

        GameFinishOptions result = new ToGameFinishOptions(json.toString()).parse();

        assertNotNull(result);
        assertEquals("https://example.com", result.openUrl);
        assertNull(result.openGameInstance);
        assertNull(result.openStory);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameFinishOptions parser = new ToGameFinishOptions("{}");

        assertEquals("GameFinishOptions", parser.getModelClassName());
    }
}
