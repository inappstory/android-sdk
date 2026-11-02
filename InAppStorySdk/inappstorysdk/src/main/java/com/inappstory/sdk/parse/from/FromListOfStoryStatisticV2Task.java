package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.stories.statistic.StoryStatisticV2Task;

import java.util.List;

public class FromListOfStoryStatisticV2Task
        extends ListOfModelToStringParser<StoryStatisticV2Task, FromStoryStatisticV2Task> {
    public FromListOfStoryStatisticV2Task(List<StoryStatisticV2Task> model) {
        super(model);
    }

    @Override
    protected FromStoryStatisticV2Task createParserInstance(StoryStatisticV2Task object) {
        return new FromStoryStatisticV2Task(object);
    }
}
