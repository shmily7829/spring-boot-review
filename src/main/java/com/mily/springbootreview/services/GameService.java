package com.mily.springbootreview.services;

import com.mily.springbootreview.data.request.GuessPlayerNumberRequest;
import com.mily.springbootreview.data.request.SetPlayerNumberRequest;
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

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public Game createGame() {

        Game game = new Game();

        Player player1 = new Player(game.getPlayer1Id());
        Player player2 = new Player(game.getPlayer2Id());
        playerRepository.save(player1);
        playerRepository.save(player2);

        return gameRepository.save(game);
    }

    public void setPlayerNumber(SetPlayerNumberRequest request,
                                String gameId,
                                String playerId) {

        Game game = findGame(gameId);
        game.setGameState(GameState.SETTING_ANSWER);
        gameRepository.save(game);

        if (!game.hasPlayer(playerId)) {
            throw new NotFoundException(String.format("The player %s doesn't exist.", playerId));
        }

        Player player = findPlayer(playerId);

        //答案已設置過
        if (player.hasAnswer()) {
            throw new NumberFormatException("The player has set up his answer. He can’t change his answer.");
        }

        player.setAnswer(request.getNumber());
        playerRepository.save(player);
    }

    public GuessPlayerNumberData guessPlayerNumber(GuessPlayerNumberRequest request, String gameId) {

        Game game = findGame(gameId);

        String guesserId = request.getGuesserId();
        String opponentId = getOpponentId(guesserId, game.getPlayer1Id(), game.getPlayer2Id());
        String guessNumber = request.getNumber();

        //設置遊戲狀態
        game.setGameState(GameState.GUESSING);

        //不在猜測者的回合中
        if (!game.getTurnPlayerId().equals(guesserId)) {
            throw new NotPlayerTurnException("The player can only guess during his turn!");
        }

        //取出猜測者和對手
        Player guesser = findPlayer(guesserId);
        Player opponent = findPlayer(opponentId);

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

    private String getOpponentId(String guesserId, String player1Id, String player2Id) {
        return guesserId.equals(player1Id) ? player2Id : player1Id;
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
