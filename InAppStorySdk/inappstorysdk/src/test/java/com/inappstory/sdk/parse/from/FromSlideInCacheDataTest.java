package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.inappmessage.domain.stedata.SlideInCacheData;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;
import org.junit.Test;

public class FromSlideInCacheDataTest {

    @Test
    public void parse_returnsValidJsonString_whenModelIsPopulated() throws Exception {
        SlideInCacheData model = new SlideInCacheData();
        model.index = 3;
        model.status = 2; // int значение вместо строки

        FromSlideInCacheData parser = new FromSlideInCacheData(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals(3, json.getInt(ModelFields.SlideInCacheData.index));
        assertEquals(2, json.getInt(ModelFields.SlideInCacheData.status)); // Используем getInt() вместо getString()
    }

    @Test
    public void parse_returnsNull_whenModelIsNull() {
        FromSlideInCacheData parser = new FromSlideInCacheData(null);
        // ModelToStringParser перехватывает NPE и возвращает null
        assertNull(parser.parse());
    }

    @Test
    public void parse_handlesDefaultIntValues() throws Exception {
        SlideInCacheData model = new SlideInCacheData();
        FromSlideInCacheData parser = new FromSlideInCacheData(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        // Примитивные int значения всегда добавляются в JSON (даже если это 0)
        assertEquals(0, json.getInt(ModelFields.SlideInCacheData.index));
        assertEquals(0, json.getInt(ModelFields.SlideInCacheData.status));
    }
}