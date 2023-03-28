package com.mily.springbootreview.entities;

public enum GameStateEnum {
    SETTING_ANSWER("setting-answer"),
    GUESSING("guessing");

    private final String state;

    GameStateEnum(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}