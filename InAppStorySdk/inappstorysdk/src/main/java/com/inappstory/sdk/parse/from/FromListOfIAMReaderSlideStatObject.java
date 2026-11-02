package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideStatObject;

import java.util.List;

public class FromListOfIAMReaderSlideStatObject
        extends ListOfModelToStringParser<IAMReaderSlideStatObject, FromIAMReaderSlideStatObject> {
    public FromListOfIAMReaderSlideStatObject(List<IAMReaderSlideStatObject> model) {
        super(model);
    }

    @Override
    protected FromIAMReaderSlideStatObject createParserInstance(IAMReaderSlideStatObject object) {
        return new FromIAMReaderSlideStatObject(object);
    }
}
