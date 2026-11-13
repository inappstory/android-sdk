package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameArchiveItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToGameArchiveItemTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedGameArchiveItem_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameArchiveItem.item, "index.html");
        json.put(ModelFields.GameArchiveItem.sha1, "da39a3ee5e6b4b0d3255bfef95601890afd80709");

        GameArchiveItem result = new ToGameArchiveItem(json).parse();

        assertNotNull(result);
        assertEquals("index.html", result.item);
        assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", result.sha1);
    }

    @Test
    public void parse_returnsItemWithNullSha1_whenOnlyRequiredItemProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.GameArchiveItem.item, "assets/main.js");

        GameArchiveItem result = new ToGameArchiveItem(json).parse();

        assertNotNull(result);
        assertEquals("assets/main.js", result.item);
        assertNull(result.sha1);
    }

    // --- Обработка null и отсутствия обязательного поля ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullOrPassedNull() {
        assertNull(new ToGameArchiveItem(null).parse());
        assertNull(new ToGameArchiveItem(new JSONObject()).parseObject(null));
    }

    @Test
    public void parse_returnsNull_whenRequiredItemFieldIsMissing() {
        assertNull(new ToGameArchiveItem(new JSONObject()).parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenRequiredItemHasInvalidDataType() throws JSONException {
        JSONObject intItem = new JSONObject().put(ModelFields.GameArchiveItem.item, 123);
        JSONObject objItem = new JSONObject().put(ModelFields.GameArchiveItem.item, new JSONObject());
        JSONObject arrItem = new JSONObject().put(ModelFields.GameArchiveItem.item, new JSONArray());
        JSONObject nullItem = new JSONObject().put(ModelFields.GameArchiveItem.item, JSONObject.NULL);

        assertNull(new ToGameArchiveItem(intItem).parse());
        assertNull(new ToGameArchiveItem(objItem).parse());
        assertNull(new ToGameArchiveItem(arrItem).parse());
        assertNull(new ToGameArchiveItem(nullItem).parse());
    }

    @Test
    public void parse_setsSha1ToNull_whenSha1HasInvalidDataTypeOrLiteralNull() throws JSONException {
        JSONObject intSha1 = new JSONObject()
                .put(ModelFields.GameArchiveItem.item, "style.css")
                .put(ModelFields.GameArchiveItem.sha1, 999);

        JSONObject objSha1 = new JSONObject()
                .put(ModelFields.GameArchiveItem.item, "style.css")
                .put(ModelFields.GameArchiveItem.sha1, new JSONObject());

        JSONObject literalNullSha1 = new JSONObject()
                .put(ModelFields.GameArchiveItem.item, "style.css")
                .put(ModelFields.GameArchiveItem.sha1, " null ");

        GameArchiveItem res1 = new ToGameArchiveItem(intSha1).parse();
        GameArchiveItem res2 = new ToGameArchiveItem(objSha1).parse();
        GameArchiveItem res3 = new ToGameArchiveItem(literalNullSha1).parse();

        assertNotNull(res1);
        assertEquals("style.css", res1.item);
        assertNull(res1.sha1);

        assertNotNull(res2);
        assertNull(res2.sha1);

        assertNotNull(res3);
        assertNull(res3.sha1);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToGameArchiveItem parser = new ToGameArchiveItem(new JSONObject());

        assertEquals("GameArchiveItem", parser.getModelClassName());
    }
}
