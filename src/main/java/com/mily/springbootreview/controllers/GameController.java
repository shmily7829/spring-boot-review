package com.mily.springbootreview.controllers;

import com.mily.springbootreview.data.request.GuessPlayerNumberRequest;
import com.mily.springbootreview.data.request.SetPlayerNumberRequest;
import com.mily.springbootreview.data.response.GameData;
import com.mily.springbootreview.data.response.GameStateData;
import com.mily.springbootreview.data.response.GuessPlayerNumberData;
import com.mily.springbootreview.data.response.Response;
import com.mily.springbootreview.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameController {

    GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @CrossOrigin
    @PostMapping("/v1/games")
    public Response<GameData> createGame() {

        GameData game = gameService.createGame();

        Response<GameData> response = new Response<>();
        response.setData(game);
        response.setMessage("The game has been created.");

        return response;
    }
    @CrossOrigin
    @PutMapping("v1/games/{gameId}/players/{playerId}/answer")
    public Response<GameData> setPlayerNumber(@RequestBody SetPlayerNumberRequest request,
                                              @PathVariable String gameId,
                                              @PathVariable String playerId) {
        //res body
        Response<GameData> response = new Response<>();
        gameService.setPlayerNumber(request, gameId, playerId);
        response.setMessage("The answer has been set.");
        return response;
    }
    @CrossOrigin
    @PostMapping("/v1/games/{gameId}/guess")
    public Response<GuessPlayerNumberData> guessPlayerNumber(@RequestBody GuessPlayerNumberRequest request,
                                                             @PathVariable String gameId) {
        GuessPlayerNumberData guessData = gameService.guessPlayerNumber(request, gameId);

        Response<GuessPlayerNumberData> response = new Response<>();
        response.setData(guessData);

        String winnerId = guessData.getWinnerId();
        String message = guessData.hasWinner() ? String.format("The player %s wins!", winnerId) : guessData.getResult();
        response.setMessage(message);

        return response;
    }
    @CrossOrigin
    @GetMapping("/v1/games/{gameId}")
    public ResponseEntity<Response<GameStateData>> getGameState(@PathVariable String gameId) {

        GameStateData gameStateData = gameService.getGameState(gameId);

        Response<GameStateData> response = new Response<>();
        response.setData(gameStateData);

        return ResponseEntity.ok(response);
    }
}
