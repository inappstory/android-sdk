package com.inappstory.sdk.parse.from;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public abstract class ListOfModelToStringParser<T, M extends ModelToJSONObjectParser<T>> {

    private final List<T> listOfModel;

    public ListOfModelToStringParser(List<T> listOfModel) {
        this.listOfModel = new ArrayList<>(listOfModel);
    }

    protected abstract M createParserInstance(T object);

    public final String parse() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (T model : listOfModel) {
                jsonArray.put(createParserInstance(model).parseToJSONObject());
            }
            return jsonArray.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
