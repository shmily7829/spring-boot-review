package com.mily.springbootreview.services;

import com.mily.springbootreview.data.GameState;
import com.mily.springbootreview.data.request.GuessPlayerNumberRequest;
import com.mily.springbootreview.data.response.GuessPlayerNumberData;
import com.mily.springbootreview.exceptions.NumberFormatException;
import com.mily.springbootreview.exceptions.StateFormatException;
import com.mily.springbootreview.exceptions.NotFoundException;
import com.mily.springbootreview.data.Player;
import com.mily.springbootreview.data.request.SetPlayerNumberRequest;
import com.mily.springbootreview.data.Game;
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

        if (!game.hasPlayer(playerId)) {
            throw new NotFoundException(String.format("The player %s doesn't exist.", playerId));
        }

        Player player = findPlayer(playerId);

        //答案已設置
        if (player.hasAnswer()) {
            throw new NumberFormatException("The player has set up his answer. He can’t change his answer.");
        }

        String number = request.getNumber();

        //檢查number數字: 必須為四位不重複的數字 0-9
        player.setAnswer(number);
        playerRepository.save(player);
    }

    /*
    * - `error message` 根據違反不同的前置條件而有不同的內容：
        - 違反前置條件一：`The game <gameId> doesn't exist.`
        - 違反前置條件二：`The players must set their answers before they guess.`
        - 違反前置條件三：`The player can only guess during his turn!`
        - 違反不變條件一：`The guess must be 4 non-repeating digits.`
    */
    public GuessPlayerNumberData guessPlayerNumber(GuessPlayerNumberRequest request, String gameId) {

        //- **前置條件:**
        //1. 此遊戲存在。
        Game game = findGame(gameId);

        String player1Id = game.getPlayer1Id();
        String player2Id = game.getPlayer2Id();

        //取出猜測者和對手
        String guesserId = request.getGuesserId();
        String opponentId = guesserId.equals(player1Id) ? player2Id : player1Id;

        Player guesser = findPlayer(guesserId);
        Player opponent = findPlayer(opponentId);

        //2. 此遊戲已進入猜謎階段。
        if (game.getGameStateEnum() != (GameState.GUESSING)) {
            throw new StateFormatException("The game's state is not guessing.");
        }

        //3. 雙方玩家必須設置好答案
        if (!guesser.hasAnswer() || !opponent.hasAnswer()) {
            throw new NotFoundException("The players must set their answers before they guess.");
        }

//        //4. 正輪到此猜謎者的玩家回合。
//        if (!game.getGuesser().equals(guesser)) {
//            throw new NotPlayerTurnException("The player can only guess during his turn!");
//        }

        GuessPlayerNumberData guessResult = new GuessPlayerNumberData();

        //play guess
        //guesser猜的數字和對手的答案做比對
        String result = guesser.guessNumber(request.getNumber(), opponent);
        if ("4A".equals(result)) {
            guessResult.setWinnerId(guesser.getPlayerId());
        }
        guessResult.setResult(result);
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
}
