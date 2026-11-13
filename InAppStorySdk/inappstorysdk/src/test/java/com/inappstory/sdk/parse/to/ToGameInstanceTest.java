package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameInstanceTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedGameInstance_whenValidIdProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameInstance.id, "instance_999");

        GameInstance result = new ToGameInstance(json).parse();

        assertNotNull(result);
        assertEquals("instance_999", result.id);
    }

    // --- Обработка null и отсутствия обязательного id ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToGameInstance(null).parse());
        assertNull(new ToGameInstance(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() {
        assertNull(new ToGameInstance(new JSONObject()).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenIdHasInvalidDataType() throws JSONException {
        JSONObject intId = new JSONObject().put(ModelFields.GameInstance.id, 12345);
        JSONObject boolId = new JSONObject().put(ModelFields.GameInstance.id, false);
        JSONObject objId = new JSONObject().put(ModelFields.GameInstance.id, new JSONObject());
        JSONObject arrId = new JSONObject().put(ModelFields.GameInstance.id, new JSONArray());
        JSONObject nullId = new JSONObject().put(ModelFields.GameInstance.id, JSONObject.NULL);

        assertNull(new ToGameInstance(intId).parse());
        assertNull(new ToGameInstance(boolId).parse());
        assertNull(new ToGameInstance(objId).parse());
        assertNull(new ToGameInstance(arrId).parse());
        assertNull(new ToGameInstance(nullId).parse());
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameInstance parser = new ToGameInstance(new JSONObject());

        assertEquals("GameInstance", parser.getModelClassName());
    }
}
