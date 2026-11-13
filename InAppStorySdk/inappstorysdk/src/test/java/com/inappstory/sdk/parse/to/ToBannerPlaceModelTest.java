package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.BannerPlaceModel;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToBannerPlaceModelTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedBannerPlaceModel_whenValidJsonProvided() throws JSONException {
        JSONObject banner1 = new JSONObject().put(ModelFields.Banner.id, 101);
        JSONObject banner2 = new JSONObject().put(ModelFields.Banner.id, 102);

        JSONArray bannersArray = new JSONArray();
        bannersArray.put(banner1);
        bannersArray.put(banner2);

        JSONObject root = new JSONObject();
        root.put(ModelFields.BannerPlaceModel.id, 7);
        root.put(ModelFields.BannerPlaceModel.banners, bannersArray);

        BannerPlaceModel result = new ToBannerPlaceModel(root.toString()).parse();

        assertNotNull(result);
        assertEquals(7, result.id);
        assertNotNull(result.banners);
        assertEquals(2, result.banners.size());
        assertEquals(101, result.banners.get(0).id);
        assertEquals(102, result.banners.get(1).id);
    }

    @Test
    public void parse_returnsModelWithNullBanners_whenOnlyRequiredIdProvided() throws JSONException {
        JSONObject root = new JSONObject();
        root.put(ModelFields.BannerPlaceModel.id, 15);

        BannerPlaceModel result = new ToBannerPlaceModel(root.toString()).parse();

        assertNotNull(result);
        assertEquals(15, result.id);
        assertNull(result.banners);
    }

    @Test
    public void parse_returnsModelWithEmptyBannersList_whenBannersArrayIsEmpty() throws JSONException {
        JSONObject root = new JSONObject();
        root.put(ModelFields.BannerPlaceModel.id, 15);
        root.put(ModelFields.BannerPlaceModel.banners, new JSONArray());

        BannerPlaceModel result = new ToBannerPlaceModel(root.toString()).parse();

        assertNotNull(result);
        assertEquals(15, result.id);
        assertNotNull(result.banners);
        assertTrue(result.banners.isEmpty());
    }

    // --- Проверка null, битого JSON и отсутствия обязательного id ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToBannerPlaceModel(null).parse());
        assertNull(new ToBannerPlaceModel("").parse());
        assertNull(new ToBannerPlaceModel("{broken_json").parse());
        // Валидный JSON, но массив вместо объекта на верхнем уровне
        assertNull(new ToBannerPlaceModel("[]").parse());
    }

    @Test
    public void parse_returnsNull_whenRequiredIdIsMissing() {
        assertNull(new ToBannerPlaceModel("{}").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_returnsNull_whenIdHasInvalidDataType() throws JSONException {
        JSONObject stringId = new JSONObject().put(ModelFields.BannerPlaceModel.id, "not_an_int");
        JSONObject objectId = new JSONObject().put(ModelFields.BannerPlaceModel.id, new JSONObject());
        JSONObject arrayId = new JSONObject().put(ModelFields.BannerPlaceModel.id, new JSONArray());
        JSONObject nullId = new JSONObject().put(ModelFields.BannerPlaceModel.id, JSONObject.NULL);

        assertNull(new ToBannerPlaceModel(stringId.toString()).parse());
        assertNull(new ToBannerPlaceModel(objectId.toString()).parse());
        assertNull(new ToBannerPlaceModel(arrayId.toString()).parse());
        assertNull(new ToBannerPlaceModel(nullId.toString()).parse());
    }

    @Test
    public void parse_setsBannersToNull_whenBannersFieldHasInvalidDataType() throws JSONException {
        JSONObject stringInsteadOfArray = new JSONObject();
        stringInsteadOfArray.put(ModelFields.BannerPlaceModel.id, 1);
        stringInsteadOfArray.put(ModelFields.BannerPlaceModel.banners, "not_an_array");

        JSONObject intInsteadOfArray = new JSONObject();
        intInsteadOfArray.put(ModelFields.BannerPlaceModel.id, 2);
        intInsteadOfArray.put(ModelFields.BannerPlaceModel.banners, 12345);

        JSONObject objectInsteadOfArray = new JSONObject();
        objectInsteadOfArray.put(ModelFields.BannerPlaceModel.id, 3);
        objectInsteadOfArray.put(ModelFields.BannerPlaceModel.banners, new JSONObject().put("k", "v"));

        BannerPlaceModel resFromString = new ToBannerPlaceModel(stringInsteadOfArray.toString()).parse();
        BannerPlaceModel resFromInt = new ToBannerPlaceModel(intInsteadOfArray.toString()).parse();
        BannerPlaceModel resFromObj = new ToBannerPlaceModel(objectInsteadOfArray.toString()).parse();

        // Сама модель BannerPlaceModel создаётся, а banners становится null
        assertNotNull(resFromString);
        assertEquals(1, resFromString.id);
        assertNull(resFromString.banners);

        assertNotNull(resFromInt);
        assertEquals(2, resFromInt.id);
        assertNull(resFromInt.banners);

        assertNotNull(resFromObj);
        assertEquals(3, resFromObj.id);
        assertNull(resFromObj.banners);
    }

    @Test
    public void parse_setsBannersToNull_whenBannersArrayContainsInvalidElementTypes() throws JSONException {
        JSONArray arrayWithInvalidItem = new JSONArray();
        arrayWithInvalidItem.put(new JSONObject().put(ModelFields.Banner.id, 10));
        arrayWithInvalidItem.put("invalid_banner_element");

        JSONObject root = new JSONObject();
        root.put(ModelFields.BannerPlaceModel.id, 5);
        root.put(ModelFields.BannerPlaceModel.banners, arrayWithInvalidItem);

        BannerPlaceModel result = new ToBannerPlaceModel(root.toString()).parse();

        // ToBannerList вернёт null, но сам BannerPlaceModel сохранится с валидным id
        assertNotNull(result);
        assertEquals(5, result.id);
        assertNull(result.banners);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToBannerPlaceModel parser = new ToBannerPlaceModel("{}");

        assertEquals("BannerPlaceModel", parser.getModelClassName());
    }
}