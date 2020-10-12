package io.casestory.sdk.stories.api.models;

public class StoryLinkObject {
    public String getType() {
        return type;
    }

    public StoryLink getLink() {
        return link;
    }

    String type;
    StoryLink link;

    public class StoryLink {
        public String getType() {
            return type;
        }

        public String getTarget() {
            return target;
        }

        String type;
        String target;
    }
}
