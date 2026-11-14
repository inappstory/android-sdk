package com.inappstory.sdk.parse.to;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

public class ToStoryStatisticV2TaskListTest {

    // --- Успешный парсинг ---

    @Test
    public void parse_returnsTaskList_whenConstructedWithValidJsonArrayString() throws JSONException {
        JSONObject task1 = new JSONObject();
        task1.put(ModelFields.StoryStatisticV2Task.event, "slide_view");
        task1.put(ModelFields.StoryStatisticV2Task.slideIndex, 1);

        JSONObject task2 = new JSONObject();
        task2.put(ModelFields.StoryStatisticV2Task.event, "click");
        task2.put(ModelFields.StoryStatisticV2Task.slideIndex, 2);

        JSONArray array = new JSONArray();
        array.put(task1);
        array.put(task2);

        ToStoryStatisticV2TaskList parser = new ToStoryStatisticV2TaskList(array.toString());
        List<StoryStatisticV2Task> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("slide_view", result.get(0).event);
        assertEquals(Integer.valueOf(1), result.get(0).slideIndex);
        assertEquals("click", result.get(1).event);
        assertEquals(Integer.valueOf(2), result.get(1).slideIndex);
    }

    @Test
    public void parse_returnsEmptyList_whenConstructedWithEmptyJsonArrayString() {
        ToStoryStatisticV2TaskList parser = new ToStoryStatisticV2TaskList("[]");
        List<StoryStatisticV2Task> result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Проверка null и невалидных входных данных ---

    @Test
    public void parse_returnsNull_whenConstructedWithNullString() {
        ToStoryStatisticV2TaskList parser = new ToStoryStatisticV2TaskList(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithEmptyString() {
        ToStoryStatisticV2TaskList parser = new ToStoryStatisticV2TaskList("");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenConstructedWithMalformedJsonString() {
        ToStoryStatisticV2TaskList parser = new ToStoryStatisticV2TaskList("{invalid_json");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenStringIsNotAJsonArray() {
        ToStoryStatisticV2TaskList parser = new ToStoryStatisticV2TaskList("{}");
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenArrayContainsNonJsonObjectElements() throws JSONException {
        JSONArray arrayWithString = new JSONArray();
        arrayWithString.put(new JSONObject().put(ModelFields.StoryStatisticV2Task.event, "view"));
        arrayWithString.put("not_a_json_object");

        JSONArray arrayWithNumber = new JSONArray().put(42);
        JSONArray arrayWithNestedArray = new JSONArray().put(new JSONArray());
        JSONArray arrayWithJsonNull = new JSONArray().put(JSONObject.NULL);

        // getJSONObject(i) выбросит JSONException, если элемент не является объектом,
        // что приведет к возврату null из-за блока try-catch в методе parseString.
        assertNull(new ToStoryStatisticV2TaskList(arrayWithString.toString()).parse());
        assertNull(new ToStoryStatisticV2TaskList(arrayWithNumber.toString()).parse());
        assertNull(new ToStoryStatisticV2TaskList(arrayWithNestedArray.toString()).parse());
        assertNull(new ToStoryStatisticV2TaskList(arrayWithJsonNull.toString()).parse());
    }

    // --- Имя модели ---

    @Test
    public void getModelClassName_returnsExpectedName() {
        ToStoryStatisticV2TaskList parser = new ToStoryStatisticV2TaskList("[]");
        assertEquals("List of GameLog", parser.getModelClassName());
    }
}