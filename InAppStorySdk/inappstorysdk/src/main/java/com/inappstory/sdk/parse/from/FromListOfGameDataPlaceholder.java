package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.GameDataPlaceholder;

import java.util.List;

public class FromListOfGameDataPlaceholder
        extends ListOfModelToStringParser<GameDataPlaceholder, FromGameDataPlaceholder> {
    public FromListOfGameDataPlaceholder(List<GameDataPlaceholder> model) {
        super(model);
    }

    @Override
    protected FromGameDataPlaceholder createParserInstance(GameDataPlaceholder object) {
        return new FromGameDataPlaceholder(object);
    }
}
