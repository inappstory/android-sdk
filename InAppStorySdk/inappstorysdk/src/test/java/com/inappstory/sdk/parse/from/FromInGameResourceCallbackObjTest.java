package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.inappstory.sdk.game.cache.InGameResourceCallbackObj;
import com.inappstory.sdk.game.cache.InGameResourceDownloadResultObj;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;
import org.junit.Test;

public class FromInGameResourceCallbackObjTest {

    @Test
    public void parse_returnsValidJsonString_whenModelIsFullyPopulated() throws Exception {
        InGameResourceCallbackObj model = new InGameResourceCallbackObj();
        model.id = "callback_123";
        
        InGameResourceDownloadResultObj response = new InGameResourceDownloadResultObj();
        response.result = true;
        response.reason = "downloaded";
        model.response = response;

        FromInGameResourceCallbackObj parser = new FromInGameResourceCallbackObj(model);
        String result = parser.parse();

        assertNotNull(result);
        JSONObject json = new JSONObject(result);
        assertEquals("callback_123", json.getString(ModelFields.InGameResourceCallbackObj.id));
        
        JSONObject responseJson = json.getJSONObject(ModelFields.InGameResourceCallbackObj.response);
        assertEquals(true, responseJson.getBoolean(ModelFields.InGameResourceDownloadResultObj.result));
        assertEquals("downloaded", responseJson.getString(ModelFields.InGameResourceDownloadResultObj.reason));
    }

    @Test
    public void parse_returnsNull_whenModelIsNull() {
        FromInGameResourceCallbackObj parser = new FromInGameResourceCallbackObj(null);
        // ModelToStringParser перехватывает NPE и возвращает null
        assertNull(parser.parse());
    }

    @Test
    public void parse_returnsNull_whenResponseModelIsNull() {
        InGameResourceCallbackObj model = new InGameResourceCallbackObj();
        model.id = "callback_123";
        model.response = null; // Вызовет NPE внутри new FromInGameResourceDownloadResultObj(null).parseToJSONObject()

        FromInGameResourceCallbackObj parser = new FromInGameResourceCallbackObj(model);
        
        // ModelToStringParser перехватывает это исключение и возвращает null
        assertNull(parser.parse());
    }
}