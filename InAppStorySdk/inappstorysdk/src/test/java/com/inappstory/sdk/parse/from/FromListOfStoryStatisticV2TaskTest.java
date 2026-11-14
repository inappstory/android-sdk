package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FromListOfStoryStatisticV2TaskTest {

    @Test
    public void parse_returnsValidJsonArrayString_whenListIsPopulated() throws JSONException {
        StoryStatisticV2Task task1 = new StoryStatisticV2Task();
        task1.event = "view";
        task1.storyId = "story_1";

        StoryStatisticV2Task task2 = new StoryStatisticV2Task();
        task2.event = "click";
        task2.storyId = "story_2";

        List<StoryStatisticV2Task> list = Arrays.asList(task1, task2);
        FromListOfStoryStatisticV2Task parser = new FromListOfStoryStatisticV2Task(list);

        String result = parser.parse();
        assertNotNull(result);

        JSONArray jsonArray = new JSONArray(result);
        assertEquals(2, jsonArray.length());
        assertEquals("view", jsonArray.getJSONObject(0).getString(ModelFields.StoryStatisticV2Task.event));
        assertEquals("story_1", jsonArray.getJSONObject(0).getString(ModelFields.StoryStatisticV2Task.storyId));
        assertEquals("click", jsonArray.getJSONObject(1).getString(ModelFields.StoryStatisticV2Task.event));
        assertEquals("story_2", jsonArray.getJSONObject(1).getString(ModelFields.StoryStatisticV2Task.storyId));
    }

    @Test
    public void parse_returnsEmptyJsonArrayString_whenListIsEmpty() throws JSONException {
        FromListOfStoryStatisticV2Task parser = new FromListOfStoryStatisticV2Task(Collections.emptyList());
        String result = parser.parse();

        assertNotNull(result);
        assertEquals("[]", result);

        JSONArray jsonArray = new JSONArray(result);
        assertEquals(0, jsonArray.length());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throwsNPE_whenListIsNull() {
        new FromListOfStoryStatisticV2Task(null);
    }

    @Test
    public void parse_returnsNull_whenListContainsNullElement() {
        List<StoryStatisticV2Task> list = new ArrayList<>();
        list.add(new StoryStatisticV2Task());
        list.add(null);

        FromListOfStoryStatisticV2Task parser = new FromListOfStoryStatisticV2Task(list);

        assertNull(parser.parse());
    }
}