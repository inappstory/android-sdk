package com.inappstory.sdk.inappmessage.domain.reader;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IAMReaderSlideState {

    int contentStatus = 0; //0 - empty, 1 - layout loaded, 2 - render ready + slides replaced, 3 - show_slides loaded

    int slideJSStatus = 0; //0 - none, 1 - loaded, 2 - started, 3 - paused?


    boolean renderReady = false;

    List<String> slides = new ArrayList<>();

    Map<String, Object> cardAppearance;
    Pair<Integer, Integer> safeArea = new Pair<>(0, 0);

    public Map<String, Object> cardAppearance() {
        return cardAppearance != null ? cardAppearance : new HashMap<>();
    }

    public Pair<Integer, Integer> safeArea() {
        return safeArea != null ? safeArea : new Pair<>(0, 0);
    }

    public IAMReaderSlideState cardAppearance(Map<String, Object> cardAppearance) {
        this.cardAppearance = cardAppearance;
        return this;
    }

    public IAMReaderSlideState safeArea(Pair<Integer, Integer> safeArea) {
        this.safeArea = safeArea;
        return this;
    }


    public List<String> slides() {
        return slides;
    }

    public IAMReaderSlideState slides(List<String> slides) {
        this.slides = slides;
        return this;
    }

    public boolean renderReady() {
        return renderReady;
    }

    public IAMReaderSlideState renderReady(boolean renderReady) {
        this.renderReady = renderReady;
        return this;
    }


    public int contentStatus() {
        return contentStatus;
    }

    public IAMReaderSlideState contentStatus(int contentStatus) {
        this.contentStatus = contentStatus;
        return this;
    }

    public int slideJSStatus() {
        return slideJSStatus;
    }

    public IAMReaderSlideState slideJSStatus(int slideJSStatus) {
        this.slideJSStatus = slideJSStatus;
        return this;
    }

    String content;

    public String content() {
        return content;
    }

    public IAMReaderSlideState content(String content) {
        this.content = content;
        return this;
    }

    String layout;

    public String layout() {
        return layout;
    }

    public IAMReaderSlideState layout(String layout) {
        this.layout = layout;
        return this;
    }

    int slideIndex = 0;

    int slidesTotal = 1;

    public int slidesTotal() {
        return slidesTotal;
    }

    public IAMReaderSlideState slidesTotal(int totalSlides) {
        this.slidesTotal = totalSlides;
        return this;
    }

    public int slideIndex() {
        return slideIndex;
    }

    public IAMReaderSlideState slideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
        return this;
    }


    public IAMReaderSlideState copy() {
        return new IAMReaderSlideState()
                .content(this.content)
                .slides(this.slides)
                .layout(this.layout)
                .cardAppearance(this.cardAppearance)
                .safeArea(this.safeArea)
                .slideIndex(this.slideIndex)
                .slidesTotal(this.slidesTotal)
                .renderReady(this.renderReady)
                .contentStatus(this.contentStatus)
                .slideJSStatus(this.slideJSStatus);
    }
}
