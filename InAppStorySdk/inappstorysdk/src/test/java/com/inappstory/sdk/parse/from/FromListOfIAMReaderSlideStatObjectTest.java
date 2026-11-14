package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideStatObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FromListOfIAMReaderSlideStatObjectTest {

    @Test
    public void parse_returnsValidJsonArrayString_whenListIsPopulated() throws JSONException {
        IAMReaderSlideStatObject stat1 = new IAMReaderSlideStatObject(1, 100);

        IAMReaderSlideStatObject stat2 = new IAMReaderSlideStatObject(2, 200);

        List<IAMReaderSlideStatObject> list = Arrays.asList(stat1, stat2);
        FromListOfIAMReaderSlideStatObject parser = new FromListOfIAMReaderSlideStatObject(list);

        String result = parser.parse();
        assertNotNull(result);

        JSONArray jsonArray = new JSONArray(result);
        assertEquals(2, jsonArray.length());
        assertEquals(1, jsonArray.getJSONObject(0).getInt(ModelFields.IAMReaderSlideStatObject.slideIndex));
        assertEquals(100, jsonArray.getJSONObject(0).getInt(ModelFields.IAMReaderSlideStatObject.duration));
        assertEquals(2, jsonArray.getJSONObject(1).getInt(ModelFields.IAMReaderSlideStatObject.slideIndex));
        assertEquals(200, jsonArray.getJSONObject(1).getInt(ModelFields.IAMReaderSlideStatObject.duration));
    }

    @Test
    public void parse_returnsEmptyJsonArrayString_whenListIsEmpty() {
        FromListOfIAMReaderSlideStatObject parser = new FromListOfIAMReaderSlideStatObject(Collections.emptyList());
        String result = parser.parse();

        assertNotNull(result);
        assertEquals("[]", result);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throwsNPE_whenListIsNull() {
        new FromListOfIAMReaderSlideStatObject(null);
    }

    @Test
    public void parse_returnsNull_whenListContainsNullElement() {
        List<IAMReaderSlideStatObject> list = new ArrayList<>();
        list.add(new IAMReaderSlideStatObject(1, 100));
        list.add(null);

        FromListOfIAMReaderSlideStatObject parser = new FromListOfIAMReaderSlideStatObject(list);

        // NPE при обращении к полям null-элемента перехватывается базовым классом, возвращая null
        assertNull(parser.parse());
    }
}