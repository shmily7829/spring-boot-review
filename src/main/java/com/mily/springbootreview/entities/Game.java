package com.mily.springbootreview.entities;

import com.mily.springbootreview.exceptions.NotFoundException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
 * errMsg: Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn
 * ref: https://blog.csdn.net/yangwenxue_admin/article/details/52473442
 * CascadeType
 * ref: https://openhome.cc/Gossip/EJB3Gossip/CascadeTypeFetchType.html
 * */

@Getter
@Setter
@Entity
@Table(name = "game")
public class Game {

    @Id
    @Column(name = "game_id")
    private String gameId;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "game_id")
    private List<Player> players = new ArrayList<>();

    @Column(name = "game_state")
    private GameState gameState;

    @Column(name = "turn_player_id")
    private String turnPlayerId;
    @ElementCollection
    @Column(name = "guess_history")
    private List<String> guessHistory;

    public Game() {

    }

    public Player findPlayer(String playerId) {
        for (Player player : players) {
            if (player.getPlayerId().equals(playerId)) {
                return player;
            }
        }
        throw new NotFoundException(String.format("Not found the player, playerId: %s", playerId));
    }

    public void addGuessNumber(String guess) {
        guessHistory.add(guess);
    }

    public String getOpponentId(String guesserId) {
        String player1Id = getPlayerId(0);
        String player2Id = getPlayerId(1);

        return guesserId.equals(player1Id) ? player2Id : player1Id;
    }

    public String getPlayerId(int index) {
        return players.get(index).getPlayerId();
    }

    public void setPlayerNumber(String playerId, String number) {

        Player player = findPlayer(playerId);
        player.setAnswer(number);
    }

    public boolean isGuesserTurn(String guesserId) {
        return getTurnPlayerId().equals(guesserId);
    }
}
