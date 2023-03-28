package com.mily.springbootreview.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    private GameState gameStateEnum;
    private String guesser;

    public Game() {
        setGameId(UUID.randomUUID().toString());
        setPlayer1Id(UUID.randomUUID().toString());
        setPlayer2Id(UUID.randomUUID().toString());
        setGuesser(player1Id);
        setGameStateEnum(GameState.GUESSING);
    }

    public boolean hasPlayer(String playerId) {
        return (getPlayer1Id().equals(playerId) || getPlayer2Id().equals(playerId));
    }
}
