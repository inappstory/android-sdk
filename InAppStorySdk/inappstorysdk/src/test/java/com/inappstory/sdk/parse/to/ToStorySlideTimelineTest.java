package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.core.network.content.models.StorySlideTimeline;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ToStorySlideTimelineTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsPopulatedTimeline_whenConstructedWithValidJsonObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StorySlideTimeline.foregroundColor, "#FFFFFF");
        json.put(ModelFields.StorySlideTimeline.backgroundColor, "#000000");
        json.put(ModelFields.StorySlideTimeline.isTimelineHidden, true);

        ToStorySlideTimeline parser = new ToStorySlideTimeline(json);
        StorySlideTimeline result = parser.parse();

        assertNotNull(result);
        assertEquals("#FFFFFF", result.foregroundColor);
        assertEquals("#000000", result.backgroundColor);
        assertTrue(result.isTimelineHidden);
    }

    @Test
    public void parse_returnsTimelineWithDefaultValues_whenKeysAreMissing() throws JSONException {
        JSONObject json = new JSONObject();

        ToStorySlideTimeline parser = new ToStorySlideTimeline(json);
        StorySlideTimeline result = parser.parse();

        assertNotNull(result);
        assertNull(result.foregroundColor);
        assertNull(result.backgroundColor);
        assertFalse(result.isTimelineHidden); // optBoolean по умолчанию возвращает false
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullJsonObject() {
        assertNull(new ToStorySlideTimeline(null).parse());
    }

    @Test
    public void parseObject_returnsNull_whenPassedNullDirectly() {
        ToStorySlideTimeline parser = new ToStorySlideTimeline(new JSONObject());
        assertNull(parser.parseObject(null));
    }

    @Test
    public void parse_handlesInvalidDataTypesInFields_withoutCrashing() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(ModelFields.StorySlideTimeline.foregroundColor, 123);
        json.put(ModelFields.StorySlideTimeline.backgroundColor, new JSONObject());
        json.put(ModelFields.StorySlideTimeline.isTimelineHidden, "not_a_boolean");

        ToStorySlideTimeline parser = new ToStorySlideTimeline(json);
        StorySlideTimeline result = parser.parse();

        assertNotNull(result);
        assertNull(result.foregroundColor);
        assertNull(result.backgroundColor);
        assertFalse(result.isTimelineHidden);
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStorySlideTimeline parser = new ToStorySlideTimeline(new JSONObject());
        assertEquals("StorySlideTimeline", parser.getModelClassName());
    }
}