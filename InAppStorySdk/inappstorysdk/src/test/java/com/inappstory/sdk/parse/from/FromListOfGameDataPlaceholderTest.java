package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.GameDataPlaceholder;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FromListOfGameDataPlaceholderTest {

    @Test
    public void parse_returnsValidJsonArrayString_whenListIsPopulated() throws JSONException {
        GameDataPlaceholder ph1 = new GameDataPlaceholder();
        ph1.name = "ph1";
        ph1.type = "text";
        ph1.value = "val1";

        GameDataPlaceholder ph2 = new GameDataPlaceholder();
        ph2.name = "ph2";
        ph2.type = "image";
        ph2.value = "val2";

        List<GameDataPlaceholder> list = Arrays.asList(ph1, ph2);
        FromListOfGameDataPlaceholder parser = new FromListOfGameDataPlaceholder(list);

        String result = parser.parse();
        assertNotNull(result);

        JSONArray jsonArray = new JSONArray(result);
        assertEquals(2, jsonArray.length());
        assertEquals("ph1", jsonArray.getJSONObject(0).getString(ModelFields.GameDataPlaceholder.name));
        assertEquals("text", jsonArray.getJSONObject(0).getString(ModelFields.GameDataPlaceholder.type));
        assertEquals("val1", jsonArray.getJSONObject(0).getString(ModelFields.GameDataPlaceholder.value));
        assertEquals("ph2", jsonArray.getJSONObject(1).getString(ModelFields.GameDataPlaceholder.name));
        assertEquals("image", jsonArray.getJSONObject(1).getString(ModelFields.GameDataPlaceholder.type));
        assertEquals("val2", jsonArray.getJSONObject(1).getString(ModelFields.GameDataPlaceholder.value));
    }

    @Test
    public void parse_returnsEmptyJsonArrayString_whenListIsEmpty() throws JSONException {
        FromListOfGameDataPlaceholder parser = new FromListOfGameDataPlaceholder(Collections.emptyList());
        String result = parser.parse();

        assertNotNull(result);
        assertEquals("[]", result);

        JSONArray jsonArray = new JSONArray(result);
        assertEquals(0, jsonArray.length());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throwsNPE_whenListIsNull() {
        new FromListOfGameDataPlaceholder(null);
    }

    @Test
    public void parse_returnsNull_whenListContainsNullElement() {
        List<GameDataPlaceholder> list = new ArrayList<>();
        list.add(new GameDataPlaceholder());
        list.add(null);

        FromListOfGameDataPlaceholder parser = new FromListOfGameDataPlaceholder(list);

        assertNull(parser.parse());
    }
}