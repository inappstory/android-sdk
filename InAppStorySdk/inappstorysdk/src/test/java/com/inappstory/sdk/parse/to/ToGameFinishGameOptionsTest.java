package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameFinishGameOptions;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameFinishGameOptionsTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedOptions_whenValidIdProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameFinishGameOptions.id, "game_instance_77");

        GameFinishGameOptions result = new ToGameFinishGameOptions(json).parse();

        assertNotNull(result);
        assertEquals("game_instance_77", result.id);
    }

    // --- Обработка null и отсутствия обязательного id ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToGameFinishGameOptions(null).parse());
        assertNull(new ToGameFinishGameOptions(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() {
        assertNull(new ToGameFinishGameOptions(new JSONObject()).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenIdHasInvalidDataType() throws JSONException {
        JSONObject intId = new JSONObject().put(ModelFields.GameFinishGameOptions.id, 12345);
        JSONObject boolId = new JSONObject().put(ModelFields.GameFinishGameOptions.id, true);
        JSONObject objId = new JSONObject().put(ModelFields.GameFinishGameOptions.id, new JSONObject());
        JSONObject arrId = new JSONObject().put(ModelFields.GameFinishGameOptions.id, new JSONArray());
        JSONObject nullId = new JSONObject().put(ModelFields.GameFinishGameOptions.id, JSONObject.NULL);

        assertNull(new ToGameFinishGameOptions(intId).parse());
        assertNull(new ToGameFinishGameOptions(boolId).parse());
        assertNull(new ToGameFinishGameOptions(objId).parse());
        assertNull(new ToGameFinishGameOptions(arrId).parse());
        assertNull(new ToGameFinishGameOptions(nullId).parse());
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameFinishGameOptions parser = new ToGameFinishGameOptions(new JSONObject());

        assertEquals("GameFinishGameOptions", parser.getModelClassName());
    }
}
