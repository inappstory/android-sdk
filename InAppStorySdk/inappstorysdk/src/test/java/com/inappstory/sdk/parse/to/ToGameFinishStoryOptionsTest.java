package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameFinishStoryOptions;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameFinishStoryOptionsTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedOptions_whenAllValidFieldsProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameFinishStoryOptions.id, "story_909");
        json.put(ModelFields.GameFinishStoryOptions.slideIndex, 3);

        GameFinishStoryOptions result = new ToGameFinishStoryOptions(json).parse();

        assertNotNull(result);
        assertEquals("story_909", result.id);
        assertEquals(3, result.slideIndex);
    }

    @Test
    public void parse_returnsDefaultSlideIndexZero_whenOnlyRequiredIdProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameFinishStoryOptions.id, "story_101");

        GameFinishStoryOptions result = new ToGameFinishStoryOptions(json).parse();

        assertNotNull(result);
        assertEquals("story_101", result.id);
        assertEquals(0, result.slideIndex);
    }

    // --- Обработка null и отсутствия обязательного id ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToGameFinishStoryOptions(null).parse());
        assertNull(new ToGameFinishStoryOptions(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() throws JSONException {
        JSONObject withoutId = new JSONObject()
                .put(ModelFields.GameFinishStoryOptions.slideIndex, 2);

        assertNull(new ToGameFinishStoryOptions(new JSONObject()).parse());
        assertNull(new ToGameFinishStoryOptions(withoutId).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenRequiredIdHasInvalidDataType() throws JSONException {
        JSONObject intId = new JSONObject().put(ModelFields.GameFinishStoryOptions.id, 500);
        JSONObject objId = new JSONObject().put(ModelFields.GameFinishStoryOptions.id, new JSONObject());
        JSONObject arrId = new JSONObject().put(ModelFields.GameFinishStoryOptions.id, new JSONArray());
        JSONObject nullId = new JSONObject().put(ModelFields.GameFinishStoryOptions.id, JSONObject.NULL);

        assertNull(new ToGameFinishStoryOptions(intId).parse());
        assertNull(new ToGameFinishStoryOptions(objId).parse());
        assertNull(new ToGameFinishStoryOptions(arrId).parse());
        assertNull(new ToGameFinishStoryOptions(nullId).parse());
    }

    @Test
    public void parse_usesDefaultZeroSlideIndex_whenSlideIndexHasInvalidDataType() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameFinishStoryOptions.id, "story_55");
        json.put(ModelFields.GameFinishStoryOptions.slideIndex, "not_an_int");

        GameFinishStoryOptions result = new ToGameFinishStoryOptions(json).parse();

        assertNotNull(result);
        assertEquals("story_55", result.id);
        assertEquals(0, result.slideIndex);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameFinishStoryOptions parser = new ToGameFinishStoryOptions(new JSONObject());

        assertEquals("GameFinishStoryOptions", parser.getModelClassName());
    }
}
