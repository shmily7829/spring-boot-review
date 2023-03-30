package com.mily.springbootreview.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "game")
public class Game {

    @Id
    @Column(name = "game_id")
    private String gameId;
    //Player
    @Column(name = "player1_id")
    private String player1Id;

    @Column(name = "player2_id")
    private String player2Id;

    @Column(name = "game_state")
    private GameState gameState;

    @Column(name = "turn_player_id")
    private String turnPlayerId;
    @ElementCollection
    @Column(name = "guess_history")
    private List<String> guessHistory;

    public Game() {
        setGameId(UUID.randomUUID().toString());
        setPlayer1Id(UUID.randomUUID().toString());
        setPlayer2Id(UUID.randomUUID().toString());
        setGameState(GameState.SETTING_ANSWER);
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
