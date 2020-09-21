package io.casestory.sdk.stories.serviceevents;

public class ChangeIndexEventInFragment {

    public int getIndex() {
        return index;
    }

    private int index;

    public int getCurItem() {
        return curItem;
    }

    private int curItem;

    public ChangeIndexEventInFragment(int index, int curItem) {
        this.index = index;
        this.curItem = curItem;
    }
}
