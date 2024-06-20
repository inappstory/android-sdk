package com.inappstory.sdk.packages.features.stories;

import com.inappstory.sdk.packages.features.stories.models.dto.IListStoryDTO;
import com.inappstory.sdk.packages.features.stories.models.dto.IReaderStoryDTO;
import com.inappstory.sdk.packages.features.stories.models.dto.IStatisticStoryDTO;

import java.util.List;

public interface IStoriesRepository {
    IListStoryDTO getListStory(int storyId);
    void addOrUpdateListStory(IListStoryDTO listStoryDTO);
    void addOrUpdateListStories(List<IListStoryDTO> stories);

    IStatisticStoryDTO getStatisticsStory(int storyId);
    void addOrUpdateStatisticStory(IStatisticStoryDTO listStoryDTO);
    void addOrUpdateStatisticStories(List<IStatisticStoryDTO> stories);

    IReaderStoryDTO getReaderStory(int storyId);
    void addOrUpdateReaderStory(IReaderStoryDTO readerStoryDTO);
    void addOrUpdateReaderStories(List<IReaderStoryDTO> stories);
}
//получение ленты с сервера - закидываем модели