package com.mily.springbootreview;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mily.springbootreview.data.response.Response;
import com.mily.springbootreview.entities.Game;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
class SpringBootReviewApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private HttpHeaders httpHeaders;

    @BeforeEach
    public void init() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }

    @DisplayName("創建遊戲")
    @Test
    void given_whenCreateGame_thenReturnOK() throws Exception {
        mockMvc.perform(post("/api/v1/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.gameId").exists())
                .andExpect(jsonPath("$.data.player1Id").exists())
                .andExpect(jsonPath("$.data.player2Id").exists())
                .andExpect(jsonPath("message").value("The game has been created."));
    }

    @DisplayName("玩家設置答案完成")
    @Test
    void givenExistingGameAndPlayer_whenSetNumber_thenReturnsNoContent() throws Exception {
        Game game = createGame();

        JSONObject requestBody = new JSONObject()
                .put("number", "4567");

        String gameId = game.getGameId();
        String player1Id = game.getPlayer1Id();

        setPlayerAnswer(gameId, player1Id, requestBody)
                .andExpect(status().isOk());
    }

    @DisplayName("設置階段遊戲不存在")
    @Test
    void givenExistingGameAndPlayer_whenSetNumberAndGameIdNotExist_thenReturnsBadRequest() throws Exception {
        //- 違反前置條件一：`The game <gameId> doesn't exist.`
        Game game = createGame();

        JSONObject requestBody = new JSONObject()
                .put("number", "4567");

        String gameId = "1111";
        setPlayerAnswer(gameId, "player1Id", requestBody)
                .andExpect(status().isBadRequest());
    }

    @DisplayName("設置階段玩家答案數字有重複")
    @Test
    void givenExistingGameAndPlayer_whenSetNumberAndNumberIsDuplicate_thenReturnsBadRequest() throws Exception {
        //- 違反前置條件二：`The answer must be 4 non-repeating digits.`

        //建立測試資料
        Game game = createGame();

        JSONObject requestBody = new JSONObject()
                .put("number", "4444");

        String gameId = game.getGameId();
        String player1Id = game.getPlayer1Id();

        setPlayerAnswer(gameId, player1Id, requestBody)
                .andExpect(status().isBadRequest());
    }

    @DisplayName("設置階段玩家重複設置答案")
    @Test
    void givenExistingGameAndPlayer_whenSetNumberAndNumberHasBeenSet_thenReturnsBadRequest() throws Exception {
        //- 違反不變條件一：`The player has set up his answer. He can’t change his answer.`

        //建立測試資料
        Game game = createGame();

        //第一次打API，res為OK
        JSONObject requestBody = new JSONObject()
                .put("number", "1234");

        String gameId = game.getGameId();
        String player1Id = game.getPlayer1Id();

        setPlayerAnswer(gameId, player1Id, requestBody)
                .andExpect(status().isOk());

        //第二次打API，res為badRequest
        setPlayerAnswer(gameId, player1Id, requestBody)
                .andExpect(status().isBadRequest());
    }

    @DisplayName("可以成功猜數字")
    @Test
    void givenExistingGame_whenPlayerGuessing_thenReturnsOK() throws Exception {

        Game game = createGame();

        JSONObject setNumberRequest = new JSONObject()
                .put("number", "2347");

        //設置player1的答案
        setPlayerAnswer(game.getGameId(), game.getPlayer1Id(), setNumberRequest)
                .andExpect(status().isOk());

        //設置player2的答案
        setPlayerAnswer(game.getGameId(), game.getPlayer2Id(), setNumberRequest)
                .andExpect(status().isOk());

        //玩家猜數字成功
        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "1234");

        guessPlayerNumber(game, guessNumberRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andExpect(jsonPath("$.data.result").value("0A3B"));
    }

    @DisplayName("遊戲結束且勝負揭曉")
    @Test
    void givenPlayerGuessing_whenGameOver_thenReturnOK() throws Exception {
        Game game = createGame();

        JSONObject setNumberRequest = new JSONObject()
                .put("number", "2347");

        //設置player1的答案
        setPlayerAnswer(game.getGameId(), game.getPlayer1Id(), setNumberRequest)
                .andExpect(status().isOk());

        //設置player2的答案
        setPlayerAnswer(game.getGameId(), game.getPlayer2Id(), setNumberRequest)
                .andExpect(status().isOk());

        //玩家猜數字成功
        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "2347");

        guessPlayerNumber(game, guessNumberRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andExpect(jsonPath("$.data.result").value("4A"))
                .andExpect(jsonPath("message").value(String.format("The player %s wins!", game.getPlayer1Id())));
    }

    @DisplayName("開始猜數字遊戲不存在")
    @Test
    void givenPlayerGuessing_whenGameIdNotExist_thenReturnBadRequest() throws Exception {
        Game game = new Game();
        //設置不存在的GameId
        game.setGameId("1111111");

        //猜數字的request
        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "2347");

        guessPlayerNumber(game, guessNumberRequest)
                .andExpect(status().isBadRequest());
    }

    @DisplayName("開始猜數字玩家未設置答案")
    @Test
    void givenPlayerGuessing_whenNotSetAnswer_thenReturnBadRequest() throws Exception {
        Game game = createGame();

        //猜數字的request
        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "2347");

        guessPlayerNumber(game, guessNumberRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("The players must set their answers before they guess."));
    }

    @DisplayName("開始猜數字猜測者的猜數有重複")
    @Test
    void givenPlayerGuessing_whenDuplicateGuessNumber_thenReturnBadRequest() throws Exception {
        Game game = createGame();

        JSONObject setNumberRequest = new JSONObject()
                .put("number", "5637");

        //設置player1的答案
        setPlayerAnswer(game.getGameId(), game.getPlayer1Id(), setNumberRequest)
                .andExpect(status().isOk());

        //設置player2的答案
        setPlayerAnswer(game.getGameId(), game.getPlayer2Id(), setNumberRequest)
                .andExpect(status().isOk());

        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "1111");

        guessPlayerNumber(game, guessNumberRequest)
                .andExpect(jsonPath("message").value("The number must be 4 non-repeating digits."))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("不在玩家１的回合中")
    @Test
    void givenPlayerGuessing_whenIsNotPlayer1Turn_thenReturnBadRequest() throws Exception {
        Game game = createGame();

        JSONObject setNumberRequest = new JSONObject()
                .put("number", "5637");

        //設置player1的答案
        setPlayerAnswer(game.getGameId(), game.getPlayer1Id(), setNumberRequest)
                .andExpect(status().isOk());

        //設置player2的答案
        setPlayerAnswer(game.getGameId(), game.getPlayer2Id(), setNumberRequest)
                .andExpect(status().isOk());

        //Given
        //玩家1 和玩家2
        //輪到玩家1 要猜數字
        //WHEN
        //當玩家2 想要猜數字
        //THEN
        //Return badRequest

        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer2Id())
                .put("number", "1298");

        guessPlayerNumber(game, guessNumberRequest)
                .andExpect(jsonPath("message").value("The player can only guess during his turn!"))
                .andExpect(status().isBadRequest());

//        Assert.assertEquals(game.getPlayer2Id(), game.getTurnPlayerId());
    }

    @DisplayName("取得當前遊戲狀態")
    @Test
    void getGameState() throws Exception {
        Game game = createGame();
        String gameId = game.getGameId();

        JSONObject request = new JSONObject()
                .put("gameId", gameId);

        mockMvc.perform(get("/api/v1/games/{gameId}", gameId)
                        .headers(httpHeaders)
                        .content(request.toString()))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.gameId").exists())
                .andExpect(jsonPath("$.data.player1Id").exists())
                .andExpect(jsonPath("$.data.player2Id").exists())
                .andExpect(jsonPath("$.data.turnPlayerId").exists())
                .andExpect(jsonPath("$.data.guessHistory").exists())
                .andDo(print())
                .andReturn();
    }

    private Game createGame() throws Exception {
        String result = mockMvc.perform(post("/api/v1/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.gameId").exists())
                .andExpect(jsonPath("$.data.player1Id").exists())
                .andExpect(jsonPath("$.data.player2Id").exists())
                .andExpect(jsonPath("message").value("The game has been created."))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Response<Game> response = mapper.readValue(result, new TypeReference<>() {
        });

        return response.getData();
    }

    private ResultActions setPlayerAnswer(String gameId, String playerId, JSONObject setAnswerRequest) throws Exception {
        return mockMvc.perform(put("/api/v1/games/{gameId}/players/{playerId}/answer", gameId, playerId)
                        .headers(httpHeaders)
                        .content(setAnswerRequest.toString()))
                .andExpect(jsonPath("message").exists())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());
    }

    private ResultActions guessPlayerNumber(Game game, JSONObject guessNumberRequest) throws Exception {
        return mockMvc.perform(post("/api/v1/games/{gameId}/guess", game.getGameId())
                        .headers(httpHeaders)
                        .content(guessNumberRequest.toString()))
                .andExpect(jsonPath("message").exists())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());
    }
}