package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.reader.SafeAreaInsets;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;
import org.junit.Test;

public class FromSafeAreaInsetsTest {

    @Test
    public void parse_returnsValidJsonString_whenModelIsPopulated() throws Exception {
        SafeAreaInsets model = new SafeAreaInsets();
        model.top = 10;
        model.bottom = 20;
        model.left = 5;
        model.right = 5;

        FromSafeAreaInsets parser = new FromSafeAreaInsets(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals(10, json.getInt(ModelFields.SafeAreaInsets.top));
        assertEquals(20, json.getInt(ModelFields.SafeAreaInsets.bottom));
        assertEquals(5, json.getInt(ModelFields.SafeAreaInsets.left));
        assertEquals(5, json.getInt(ModelFields.SafeAreaInsets.right));
    }

    @Test
    public void parse_returnsNull_whenModelIsNull() {
        FromSafeAreaInsets parser = new FromSafeAreaInsets(null);
        // ModelToStringParser перехватывает NPE и возвращает null
        assertNull(parser.parse());
    }
}