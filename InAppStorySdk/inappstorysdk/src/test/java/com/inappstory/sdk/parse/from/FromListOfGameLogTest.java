package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FromListOfGameLogTest {

    @Test
    public void parse_returnsValidJsonArrayString_whenListIsPopulated() throws JSONException {
        GameLog log1 = new GameLog();
        log1.message = "Log 1";
        log1.timestamp = 1000L;

        GameLog log2 = new GameLog();
        log2.message = "Log 2";
        log2.timestamp = 2000L;

        List<GameLog> list = Arrays.asList(log1, log2);
        FromListOfGameLog parser = new FromListOfGameLog(list);

        String result = parser.parse();
        assertNotNull(result);

        JSONArray jsonArray = new JSONArray(result);
        assertEquals(2, jsonArray.length());
        assertEquals("Log 1", jsonArray.getJSONObject(0).getString(ModelFields.GameLog.message));
        assertEquals(1000L, jsonArray.getJSONObject(0).getLong(ModelFields.GameLog.timestamp));
        assertEquals("Log 2", jsonArray.getJSONObject(1).getString(ModelFields.GameLog.message));
        assertEquals(2000L, jsonArray.getJSONObject(1).getLong(ModelFields.GameLog.timestamp));
    }

    @Test
    public void parse_returnsEmptyJsonArrayString_whenListIsEmpty() throws JSONException {
        FromListOfGameLog parser = new FromListOfGameLog(Collections.emptyList());
        String result = parser.parse();

        assertNotNull(result);
        assertEquals("[]", result);

        JSONArray jsonArray = new JSONArray(result);
        assertEquals(0, jsonArray.length());
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throwsNPE_whenListIsNull() {
        new FromListOfGameLog(null);
    }

    @Test
    public void parse_returnsNull_whenListContainsNullElement() {
        List<GameLog> list = new ArrayList<>();
        list.add(new GameLog());
        list.add(null);

        FromListOfGameLog parser = new FromListOfGameLog(list);

        assertNull(parser.parse());
    }
}