package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.logger.GameLog;

import java.util.List;

public class FromListOfGameLog
        extends ListOfModelToStringParser<GameLog, FromGameLog> {
    public FromListOfGameLog(List<GameLog> model) {
        super(model);
    }

    @Override
    protected FromGameLog createParserInstance(GameLog object) {
        return new FromGameLog(object);
    }
}
