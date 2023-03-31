package com.mily.springbootreview.services;

import com.mily.springbootreview.data.request.GuessPlayerNumberRequest;
import com.mily.springbootreview.data.request.SetPlayerNumberRequest;
import com.mily.springbootreview.data.response.GameData;
import com.mily.springbootreview.data.response.GameStateData;
import com.mily.springbootreview.data.response.GuessPlayerNumberData;
import com.mily.springbootreview.entities.Game;
import com.mily.springbootreview.entities.GameState;
import com.mily.springbootreview.entities.Player;
import com.mily.springbootreview.exceptions.NotFoundException;
import com.mily.springbootreview.exceptions.NotPlayerTurnException;
import com.mily.springbootreview.exceptions.NumberFormatException;
import com.mily.springbootreview.respositories.GameRepository;
import com.mily.springbootreview.respositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameData createGame() {
        Game game = new Game();
        String gameId = UUID.randomUUID().toString();
        game.setGameId(gameId);

        List<Player> players = new ArrayList<>();

        Player player1 = new Player(gameId);
        Player player2 = new Player(gameId);
        players.add(player1);
        players.add(player2);
        game.setPlayers(players);

        game.setGameState(GameState.SETTING_ANSWER);
        game.setTurnPlayerId(player1.getPlayerId());
        game = gameRepository.save(game);
        return new GameData(gameId,
                game.getPlayerId(0),
                game.getPlayerId(1));
    }

    public void setPlayerNumber(SetPlayerNumberRequest request,
                                String gameId,
                                String playerId) {

        Game game = findGame(gameId);
        game.setGameState(GameState.SETTING_ANSWER);
        game.setPlayerNumber(playerId, request.getNumber());
        gameRepository.save(game);
    }

    public GuessPlayerNumberData guessPlayerNumber(GuessPlayerNumberRequest request, String gameId) {

        Game game = findGame(gameId);

        String guesserId = request.getGuesserId();
        String opponentId = game.getOpponentId(guesserId);
        String guessNumber = request.getNumber();

        //設置遊戲狀態
        game.setGameState(GameState.GUESSING);

        //不在猜測者的回合中
        if (!game.isGuesserTurn(guesserId)) {
            throw new NotPlayerTurnException("The player can only guess during his turn!");
        }

        //取出猜測者和對手
        Player guesser = game.findPlayer(guesserId);
        Player opponent = game.findPlayer(opponentId);

        //雙方玩家必須設置好答案
        if (!guesser.hasAnswer() || !opponent.hasAnswer()) {
            throw new NotFoundException("The players must set their answers before they guess.");
        }

        GuessPlayerNumberData guessResult = new GuessPlayerNumberData();

        //猜數字
        String result = guesser.guessNumber(guessNumber, opponent);
        if ("4A".equals(result)) {
            guessResult.setWinnerId(guesser.getPlayerId());
        }
        guessResult.setResult(result);

        game.setTurnPlayerId(opponentId);
        game.addGuessNumber(guessNumber);
        gameRepository.save(game);

        return guessResult;
    }

    private Player findPlayer(String playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException(String.format("The player %s doesn't exist.", playerId)));
    }

    private Game findGame(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(String.format("The game %s doesn't exist.", gameId)));
    }

    public GameStateData getGameState(String gameId) {
        Game game = findGame(gameId);
        return new GameStateData(game);
    }
}
