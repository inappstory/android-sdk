package com.inappstory.sdk.inappmessage.domain.reader;

public class IAMReaderSlideStatState {

    private long resumedTime = 0;
    private long resumedSlideTime = 0;

    public String iterationId() {
        return iterationId;
    }

    private int[] slideTimes = null;

    private String iterationId;

    private final Object lock = new Object();

    private int currentIndex;

    public void updateSlidesCount(int count) {
        synchronized (lock) {
            if (slideTimes == null || slideTimes.length != count) {
                this.slideTimes = new int[count];
            }
        }
    }

    public void updateSlideIndex(int newIndex) {
        synchronized (lock) {
            if (slideTimes == null) return;
            if (newIndex == currentIndex) return;
            if (newIndex < 0 || newIndex >= slideTimes.length) return;
            if (currentIndex >= 0)
                slideTimes[currentIndex] += (System.currentTimeMillis() - this.resumedSlideTime);
            this.resumedSlideTime = System.currentTimeMillis();
            currentIndex = newIndex;
        }
    }

    long totalTime() {
        synchronized (lock) {
            return (System.currentTimeMillis() - this.resumedTime);
        }
    }

    public int[] totalSlideTimes() {
        synchronized (lock) {
            if (this.slideTimes == null) return null;
            int[] currentSlideTimes = new int[this.slideTimes.length];
            for (int i = 0; i < this.slideTimes.length; i++) {
                currentSlideTimes[i] = this.slideTimes[i];
            }
            if (currentIndex >= 0)
                currentSlideTimes[currentIndex] += System.currentTimeMillis() - this.resumedTime;
            return currentSlideTimes;
        }
    }


    void create(
            String iterationId
    ) {
        synchronized (lock) {
            this.currentIndex = -1;
            this.iterationId = iterationId;
            this.resumedTime = System.currentTimeMillis();
            this.resumedSlideTime = System.currentTimeMillis();
        }
    }

    void resume() {
        synchronized (lock) {
            this.resumedTime = System.currentTimeMillis();
            this.resumedSlideTime = System.currentTimeMillis();
            if (slideTimes != null) {
                slideTimes = new int[slideTimes.length];
            }
        }
    }

    void clear() {
        synchronized (lock) {
            this.iterationId = null;
            this.resumedTime = 0;
        }
    }
}
