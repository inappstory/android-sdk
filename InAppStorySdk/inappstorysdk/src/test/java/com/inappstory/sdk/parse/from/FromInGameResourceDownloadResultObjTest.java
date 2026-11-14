package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.inappstory.sdk.game.cache.InGameResourceDownloadResultObj;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;
import org.junit.Test;

public class FromInGameResourceDownloadResultObjTest {

    @Test
    public void parseToJSONObject_returnsValidJsonObject_whenModelIsPopulated() throws Exception {
        InGameResourceDownloadResultObj model = new InGameResourceDownloadResultObj();
        model.result = true;
        model.reason = "success";

        FromInGameResourceDownloadResultObj parser = new FromInGameResourceDownloadResultObj(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertTrue(result.getBoolean(ModelFields.InGameResourceDownloadResultObj.result));
        assertEquals("success", result.getString(ModelFields.InGameResourceDownloadResultObj.reason));
    }

    @Test
    public void parseToJSONObject_handlesFalseResultAndNullReason() throws Exception {
        InGameResourceDownloadResultObj model = new InGameResourceDownloadResultObj();
        model.result = false;
        model.reason = null;

        FromInGameResourceDownloadResultObj parser = new FromInGameResourceDownloadResultObj(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertFalse(result.getBoolean(ModelFields.InGameResourceDownloadResultObj.result));
        // JSONObject.put с null значением не добавляет ключ
        assertFalse(result.has(ModelFields.InGameResourceDownloadResultObj.reason));
    }

    @Test(expected = NullPointerException.class)
    public void parseToJSONObject_throwsNPE_whenModelIsNull() throws Exception {
        FromInGameResourceDownloadResultObj parser = new FromInGameResourceDownloadResultObj(null);
        parser.parseToJSONObject();
    }
}