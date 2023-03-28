package com.mily.springbootreview.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mily.springbootreview.entities.Game;
import com.mily.springbootreview.entities.GameStateEnum;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GameStateData {
    public GameStateData(Game game) {
        this.gameId = game.getGameId();
        this.player1Id = game.getPlayer1Id();
        this.player2Id = game.getPlayer2Id();
        this.gameStateEnum = game.getGameStateEnum();
        this.turnPlayerId = game.getTurnPlayerId();
        this.guessHistory = game.getGuessHistory();
    }

    private String gameId;
    private String player1Id;
    private String player2Id;
    private GameStateEnum gameStateEnum;
    private String turnPlayerId;
    private List<String> guessHistory;
}
