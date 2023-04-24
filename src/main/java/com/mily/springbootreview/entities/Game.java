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

    @Column(name = "winner_id")
    private String winnerId;

    @ElementCollection
    @Column(name = "guess_history")
    private List<String> guessHistory; //guessNumber

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

    //gameResult P1 =0.2.4.6, P2 = 1.3.5.7 只要顯示自己的
    public List<String> getResultHistory() {
        Player p1 = players.get(0);
        Player p2 = players.get(1);

        List<String> resultHistory = new ArrayList<>();

        for (int i = 0; i < guessHistory.size(); i++) {
            if (i % 2 == 0) {
                resultHistory.add(p1.guessNumber(guessHistory.get(i), p2));
            } else {
                resultHistory.add(p2.guessNumber(guessHistory.get(i), p1));
            }
        }
        return resultHistory;
    }

}
