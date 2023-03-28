package com.mily.springbootreview.controllers;

import com.mily.springbootreview.data.request.SetPlayerNumberRequest;
import com.mily.springbootreview.data.Game;
import com.mily.springbootreview.data.request.GuessPlayerNumberRequest;
import com.mily.springbootreview.data.response.GuessPlayerNumberData;
import com.mily.springbootreview.data.response.Response;
import com.mily.springbootreview.exceptions.*;
import com.mily.springbootreview.exceptions.NumberFormatException;
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

    @PostMapping("/v1/games")
    public Response<Game> createGame() {
        Game game = gameService.createGame();
        Response<Game> response = new Response<>();
        response.setData(game);
        response.setMessage("The game has been created.");

        return response;
    }

    @PutMapping("v1/games/{gameId}/players/{playerId}/answer")
    public ResponseEntity<Response<Game>> setPlayerNumber(@RequestBody SetPlayerNumberRequest request,
                                                          @PathVariable String gameId,
                                                          @PathVariable String playerId) {
        Response<Game> response = new Response<>();

        try {

            gameService.setPlayerNumber(request, gameId, playerId);

        } catch (NotFoundException | DuplicateNumberException | NumberFormatException ex) {

            response.setMessage(ex.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
        response.setMessage("The answer has been set.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/games/{gameId}/guess")
    public ResponseEntity<Response<GuessPlayerNumberData>> guessPlayerNumber(@RequestBody GuessPlayerNumberRequest request,
                                                                             @PathVariable String gameId) {
        Response<GuessPlayerNumberData> response = new Response<>();
        try {

            GuessPlayerNumberData guessData = gameService.guessPlayerNumber(request, gameId);
            response.setData(guessData);
            response.setMessage(guessData.getResult());

        } catch (NotFoundException | NotPlayerTurnException | StateFormatException ex) {
            response.setMessage(ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);

    }
}