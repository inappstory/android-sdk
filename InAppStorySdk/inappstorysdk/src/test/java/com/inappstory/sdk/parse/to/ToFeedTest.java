package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToFeedTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedFeed_whenValidJsonProvided() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.Feed.feedId, 42);
        json.put(ModelFields.Feed.hasFavorite, true);
        json.put(ModelFields.Feed.stories, new JSONArray());
        json.put(ModelFields.Feed.feedCover, new JSONArray());

        Feed result = new ToFeed(json.toString()).parse();

        assertNotNull(result);
        assertEquals(Integer.valueOf(42), result.feedId);
        assertEquals(Boolean.TRUE, result.hasFavorite);
        assertNotNull(result.stories);
        assertTrue(result.stories.isEmpty());
        assertNotNull(result.feedCover);
        assertTrue(result.feedCover.isEmpty());
    }

    @Test
    public void parse_returnsFeedWithNullFields_whenJsonIsEmpty() {
        Feed result = new ToFeed("{}").parse();

        assertNotNull(result);
        assertNull(result.feedId);
        assertNull(result.hasFavorite);
        assertNull(result.stories);
        assertNull(result.feedCover);
    }

    // --- Обработка null и битого JSON ---

    @Test
    public void parse_returnsNull_whenInputStringIsNullOrMalformed() {
        assertNull(new ToFeed(null).parse());
        assertNull(new ToFeed("").parse());
        assertNull(new ToFeed("{broken_json").parse());
        assertNull(new ToFeed("[]").parse());
    }

    // --- Проверка невалидных типов данных ---

    @Test
    public void parse_setsFieldsToNull_whenDataTypesAreInvalid() throws JSONException {
        JSONObject json = new JSONObject();
        // Строка и объект вместо Integer / Boolean
        json.put(ModelFields.Feed.feedId, "not_an_int");
        json.put(ModelFields.Feed.hasFavorite, "not_a_boolean");
        // Строка и число вместо JSONArray
        json.put(ModelFields.Feed.stories, "not_an_array");
        json.put(ModelFields.Feed.feedCover, 12345);

        Feed result = new ToFeed(json.toString()).parse();

        assertNotNull(result);
        assertNull(result.feedId);
        assertNull(result.hasFavorite);
        assertNull(result.stories);
        assertNull(result.feedCover);
    }

    @Test
    public void parse_setsListsToNull_whenArraysContainInvalidElementTypes() throws JSONException {
        JSONArray invalidStories = new JSONArray().put("not_a_story_object");
        JSONArray invalidCovers = new JSONArray().put(999);

        JSONObject json = new JSONObject();
        json.put(ModelFields.Feed.feedId, 10);
        json.put(ModelFields.Feed.stories, invalidStories);
        json.put(ModelFields.Feed.feedCover, invalidCovers);

        Feed result = new ToFeed(json.toString()).parse();

        // Сам объект Feed создаётся, но некорректные списки превращаются в null
        assertNotNull(result);
        assertEquals(Integer.valueOf(10), result.feedId);
        assertNull(result.stories);
        assertNull(result.feedCover);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToFeed parser = new ToFeed("{}");

        assertEquals("Feed", parser.getModelClassName());
    }
}
