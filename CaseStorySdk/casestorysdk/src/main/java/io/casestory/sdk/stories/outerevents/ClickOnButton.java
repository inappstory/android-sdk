package io.casestory.sdk.stories.outerevents;

public class ClickOnButton extends BaseOuterEvent {

    public int getIndex() {
        return index;
    }

    public String getLink() {
        return link;
    }

    int index;
    String link;

    public ClickOnButton(int id, String title, String tags, int slidesCount, int index, String link) {
        super(id, title, tags, slidesCount);
        this.index = index;
        this.link = link;
    }
}
