package com.mily.springbootreview.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "game")
public class Game {

    @Id
    private String gameId;
    private String player1Id;
    private String player2Id;
    private GameStateEnum gameStateEnum;
    private String turnPlayerId;
    @ElementCollection
    private List<String> guessHistory;

    public Game() {
        setGameId(UUID.randomUUID().toString());
        setPlayer1Id(UUID.randomUUID().toString());
        setPlayer2Id(UUID.randomUUID().toString());
        setGameStateEnum(GameStateEnum.SETTING_ANSWER);
        setTurnPlayerId(player1Id);
    }

    public boolean hasPlayer(String playerId) {
        return (getPlayer1Id().equals(playerId) || getPlayer2Id().equals(playerId));
    }

    public boolean turnPlayer(String playerId) {
        return getTurnPlayerId() == null || !getTurnPlayerId().equals(playerId);
    }

    public void addGuessNumber(String guess) {
        guessHistory.add(guess);
    }


}
