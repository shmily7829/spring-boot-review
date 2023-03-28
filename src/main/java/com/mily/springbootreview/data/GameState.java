package com.mily.springbootreview.data;

public enum GameState {
    SETTING_ANSWER("setting-answer"),
    GUESSING("guessing");

    private final String state;

    GameState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}