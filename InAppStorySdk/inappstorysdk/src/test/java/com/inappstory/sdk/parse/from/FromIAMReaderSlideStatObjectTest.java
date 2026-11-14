package com.inappstory.sdk.parse.from;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideStatObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;
import org.junit.Test;

public class FromIAMReaderSlideStatObjectTest {

    @Test
    public void parseToJSONObject_returnsValidJsonObject_whenModelIsPopulated() throws Exception {
        IAMReaderSlideStatObject model = new IAMReaderSlideStatObject(2, 5000);

        FromIAMReaderSlideStatObject parser = new FromIAMReaderSlideStatObject(model);
        JSONObject result = parser.parseToJSONObject();

        assertNotNull(result);
        assertEquals(2, result.getInt(ModelFields.IAMReaderSlideStatObject.slideIndex));
        assertEquals(5000, result.getInt(ModelFields.IAMReaderSlideStatObject.duration));
    }

    @Test(expected = NullPointerException.class)
    public void parseToJSONObject_throwsNPE_whenModelIsNull() throws Exception {
        FromIAMReaderSlideStatObject parser = new FromIAMReaderSlideStatObject(null);
        // ModelToJSONObjectParser не перехватывает исключения в parseToJSONObject()
        parser.parseToJSONObject();
    }
}