package com.inappstory.sdk.game.reader;

class GameDataPlaceholder {
    public GameDataPlaceholder(String type, String name, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String name;
    public String type;
    public String value;

    public GameDataPlaceholder() {
    }


}