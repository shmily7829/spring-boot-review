package com.mily.springbootreview.entities;

public enum GameState {
    SETTING_ANSWER("setting-answer"),
    GUESSING("guessing"),
    GAME_OVER("game-over");
    private final String state;

    GameState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}