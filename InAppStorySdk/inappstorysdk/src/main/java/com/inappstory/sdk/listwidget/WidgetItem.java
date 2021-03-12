package com.inappstory.sdk.listwidget;

public class WidgetItem {
    String something;


    public WidgetItem(String something) {
        super();
        this.something = something;
    }

    public String getSomething() {
        return something;
    }
    public String getSomethingPlus() {
        return something + "+";
    }

    public void setSomething(String something) {
        this.something = something;
    }

}