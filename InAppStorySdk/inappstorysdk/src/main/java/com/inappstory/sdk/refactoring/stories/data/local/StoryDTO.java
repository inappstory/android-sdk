package com.inappstory.sdk.refactoring.stories.data.local;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.LoggerTags;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.data.IReaderContentSlide;
import com.inappstory.sdk.core.data.IResource;
import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.core.network.content.models.StorySlide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryDTO implements IReaderContent {

    public int id;
    public String title;
    public String titleColor;
    public String statTitle;
    public List<Image> videoUrl;
    public HashMap<String, Object> ugcPayload;
    public String backgroundColor;
    public List<Image> image;
    public Boolean hasSwipeUp;
    public List<IReaderContentSlide> slides;
    public Integer like;
    public int slidesCount;
    public boolean favorite;
    public String deeplink;
    public boolean isOpened;
    public boolean disableClose;
    public Boolean hasLike;
    public Boolean hasAudio;
    public Boolean hasFavorite;
    public Boolean hasShare;
    public String layout;

    public StoryDTO() {
    }

    public StoryDTO(
            int id,
            String title,
            String titleColor,
            String statTitle,
            List<Image> videoUrl,
            HashMap<String, Object> ugcPayload,
            String backgroundColor,
            List<Image> image,
            Boolean hasSwipeUp,
            List<StorySlide> slides,
            Integer like,
            int slidesCount,
            boolean favorite,
            String deeplink,
            boolean isOpened,
            boolean disableClose,
            Boolean hasLike,
            Boolean hasAudio,
            Boolean hasFavorite,
            Boolean hasShare,
            String layout
    ) {
        this.id = id;
        this.title = title;
        this.titleColor = titleColor;
        this.statTitle = statTitle;
        this.videoUrl = videoUrl;
        this.ugcPayload = ugcPayload;
        this.backgroundColor = backgroundColor;
        this.image = image;
        this.hasSwipeUp = hasSwipeUp;
        if (slides != null)
            this.slides = new ArrayList<>(slides);
        this.like = like;
        this.slidesCount = slidesCount;
        this.favorite = favorite;
        this.deeplink = deeplink;
        this.isOpened = isOpened;
        this.disableClose = disableClose;
        this.hasLike = hasLike;
        this.hasAudio = hasAudio;
        this.hasFavorite = hasFavorite;
        this.hasShare = hasShare;
        this.layout = layout;
    }

    public String imageCoverByQuality(int quality) {
        if (image == null || image.isEmpty())
            return null;
        String q = Image.TYPE_MEDIUM;
        if (quality == Image.QUALITY_HIGH) {
            q = Image.TYPE_HIGH;
        }
        for (Image img : image) {
            if (img.getType().equals(q)) return img.getUrl();
        }
        return image.get(0).getUrl();
    }

    @Override
    public boolean hasFavorite() {
        return hasFavorite != null ? hasFavorite : false;
    }

    @Override
    public boolean hasLike() {
        return hasLike != null ? hasLike : false;
    }

    @Override
    public boolean hasShare() {
        return hasShare != null ? hasShare : false;
    }

    @Override
    public boolean favorite() {
        return this.favorite;
    }

    @Override
    public void like(int like) {
        this.like = like;
    }

    @Override
    public void favorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int like() {
        return like != null ? like : 0;
    }

    @Override
    public boolean isOpened() {
        return isOpened;
    }

    @Override
    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }


    @Override
    public boolean hasAudio() {
        return hasAudio != null ? hasAudio : false;
    }

    @Override
    public boolean hasSwipeUp() {
        return hasSwipeUp != null ? hasSwipeUp : false;
    }

    @Override
    public boolean disableClose() {
        return disableClose;
    }


    @Override
    public int id() {
        return id;
    }

    @Override
    public String statTitle() {
        return statTitle;
    }

    @Override
    public int slidesCount() {
        return slidesCount;
    }

    @Override
    public Map<String, Object> ugcPayload() {
        return ugcPayload;
    }

    @Override
    public String layout() {
        return layout;
    }

    @Override
    public String slideByIndex(int index) {
        IReaderContentSlide slide = slide(index);
        if (slide == null) return null;
        return slide.html();
    }

    @Override
    public List<IResource> vodResources(int index) {
        IReaderContentSlide slide = slide(index);
        if (slide == null) return null;
        return slide.vodResources();
    }

    @Override
    public List<IResource> staticResources(int index) {
        IReaderContentSlide slide = slide(index);
        if (slide == null) return null;
        return slide.staticResources();
    }

    @Override
    public List<String> placeholdersNames(int index) {
        IReaderContentSlide slide = slide(index);
        if (slide == null) return null;
        return slide.placeholdersNames();
    }

    @Override
    public Map<String, String> placeholdersMap(int index) {
        IReaderContentSlide slide = slide(index);
        if (slide == null) return null;
        return slide.placeholdersMap();
    }

    @Override
    public int actualSlidesCount() {
        if (slides != null) return slides.size();
        return 0;
    }

    @Override
    public String slideEventPayload(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return null;
        return slide.slidePayload();
    }

    @Override
    public boolean checkIfEmpty() {
        return (layout == null || slides == null || slides.isEmpty());
    }

    private IReaderContentSlide slide(int index) {
        if (slides == null || index < 0 || slides.size() <= index) {
            InAppStoryManager.showELog(
                    LoggerTags.IAS_ERROR_TAG,
                    "Slide index out of bounds: " + index + " from " + slidesCount
            );
            return null;
        }
        for (IReaderContentSlide slide : slides) {
            if (slide.index() == index) return slide;
        }
        InAppStoryManager.showELog(
                LoggerTags.IAS_ERROR_TAG,
                "Slide " + index + " not in slides array of story " + id()
        );
        return null;
    }


    @Override
    public int shareType(int slideIndex) {
        IReaderContentSlide slide = slide(slideIndex);
        if (slide == null) return 0;
        return slide.shareType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoryDTO)) return false;
        StoryDTO storyDTO = (StoryDTO) o;
        return id == storyDTO.id &&
                slidesCount == storyDTO.slidesCount &&
                favorite == storyDTO.favorite &&
                isOpened == storyDTO.isOpened &&
                disableClose == storyDTO.disableClose &&
                Objects.equals(title, storyDTO.title) &&
                Objects.equals(titleColor, storyDTO.titleColor) &&
                Objects.equals(statTitle, storyDTO.statTitle) &&
                Objects.equals(videoUrl, storyDTO.videoUrl) &&
                Objects.equals(ugcPayload, storyDTO.ugcPayload) &&
                Objects.equals(backgroundColor, storyDTO.backgroundColor) &&
                Objects.equals(image, storyDTO.image) &&
                Objects.equals(hasSwipeUp, storyDTO.hasSwipeUp) &&
                Objects.equals(slides, storyDTO.slides) &&
                Objects.equals(like, storyDTO.like) &&
                Objects.equals(deeplink, storyDTO.deeplink) &&
                Objects.equals(hasLike, storyDTO.hasLike) &&
                Objects.equals(hasAudio, storyDTO.hasAudio) &&
                Objects.equals(hasFavorite, storyDTO.hasFavorite) &&
                Objects.equals(hasShare, storyDTO.hasShare) &&
                Objects.equals(layout, storyDTO.layout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                title,
                titleColor,
                statTitle,
                videoUrl,
                ugcPayload,
                backgroundColor,
                image, hasSwipeUp,
                slides,
                like,
                slidesCount,
                favorite,
                deeplink,
                isOpened,
                disableClose,
                hasLike,
                hasAudio,
                hasFavorite,
                hasShare,
                layout
        );
    }

}
