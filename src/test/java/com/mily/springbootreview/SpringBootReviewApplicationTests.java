package com.mily.springbootreview;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mily.springbootreview.data.Game;
import com.mily.springbootreview.data.response.Response;
import com.mily.springbootreview.respositories.GameRepository;
import com.mily.springbootreview.respositories.PlayerRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    }

    @DisplayName("創建遊戲")
    @Test
    void testCreateGame() throws Exception {
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

        setAnswer(gameId, player1Id, requestBody)
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
        setAnswer(gameId, "player1Id", requestBody)
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

        setAnswer(gameId, player1Id, requestBody)
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

        setAnswer(gameId, player1Id, requestBody)
                .andExpect(status().isOk());

        //第二次打API，res為badRequest
        setAnswer(gameId, player1Id, requestBody)
                .andExpect(status().isBadRequest());
    }

    private ResultActions setAnswer(String gameId, String playerId, JSONObject requestBody) throws Exception {
        return mockMvc.perform(put("/api/v1/games/{gameId}/players/{playerId}/answer", gameId, playerId)
                        .headers(httpHeaders)
                        .content(requestBody.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
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

    @DisplayName("可以成功猜數字")
    @Test
    void givenExistingGame_whenPlayerGuessing_thenReturnsOK() throws Exception {

        Game game = createGame();

        JSONObject setNumberRequest = new JSONObject()
                .put("number", "2347");

        //設置player1的答案
        setAnswer(game.getGameId(), game.getPlayer1Id(), setNumberRequest)
                .andExpect(status().isOk());

        //設置player2的答案
        setAnswer(game.getGameId(), game.getPlayer2Id(), setNumberRequest)
                .andExpect(status().isOk());

        //玩家猜數字成功
        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "1234");

        guessNumber(game, guessNumberRequest)
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
        setAnswer(game.getGameId(), game.getPlayer1Id(), setNumberRequest)
                .andExpect(status().isOk());

        //設置player2的答案
        setAnswer(game.getGameId(), game.getPlayer2Id(), setNumberRequest)
                .andExpect(status().isOk());

        //玩家猜數字成功
        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "2347");

        guessNumber(game, guessNumberRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").exists())
                .andExpect(jsonPath("$.data.result").value("4A"))
                .andExpect(jsonPath("message").value(String.format("The player %s wins!", game.getPlayer1Id())));
    }

    @DisplayName("猜數字階段遊戲不存在")
    @Test
    void givenPlayerGuessing_whenGameIdNotExist_thenReturnBadRequest() throws Exception {
//        Game game = createGame();
//
//        JSONObject setNumberRequest = new JSONObject()
//                .put("number", "2347");
//
//        //設置player1的答案
//        setAnswer(game.getGameId(), game.getPlayer1Id(), setNumberRequest)
//                .andExpect(status().isOk());
//
//        //設置player2的答案
//        setAnswer(game.getGameId(), game.getPlayer2Id(), setNumberRequest)
//                .andExpect(status().isOk());
//
        Game game = new Game();

        //猜數字的request
        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "2347");

        //測試需求是打猜數字的api時遊戲不存在，那可以略過createGame、setAnswer的步驟直接測試guessNumber嗎
        //設置不存在的GameId
        game.setGameId("1111111");
        guessNumber(game, guessNumberRequest)
                .andExpect(status().isBadRequest());
    }

    @DisplayName("猜數字階段玩家未設置答案")
    @Test
    void givenPlayerGuessing_whenNotSetAnswer_thenReturnBadRequest() throws Exception {
        Game game = createGame();

        JSONObject setNumberRequest = new JSONObject()
                .put("number", null);

        //設置player1的答案
        setAnswer(game.getGameId(), game.getPlayer1Id(), setNumberRequest)
                .andExpect(status().isBadRequest());

        //設置player2的答案
        setAnswer(game.getGameId(), game.getPlayer2Id(), setNumberRequest)
                .andExpect(status().isBadRequest());

        //猜數字的request
        JSONObject guessNumberRequest = new JSONObject()
                .put("guesserId", game.getPlayer1Id())
                .put("number", "2347");

        guessNumber(game, guessNumberRequest)
                .andExpect(status().isBadRequest());
    }

    @DisplayName("猜數數字階段並非猜測狀態")
    @Test
    void givenPlayerGuessing_whenNotGuessingState_thenReturnBadRequest() throws Exception {

    }

    @DisplayName("猜數數字階段玩家設置答案數字有重複")
    @Test
    void givenPlayerGuessing_whenDuplicateNumber_thenReturnBadRequest() throws Exception {

    }

    private ResultActions guessNumber(Game game, JSONObject guessNumberRequest) throws Exception {
        return mockMvc.perform(post("/api/v1/games/{gameId}/guess", game.getGameId())
                        .headers(httpHeaders)
                        .content(guessNumberRequest.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("message").exists())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}

//建立測試資料
//        String gameId = UUID.randomUUID().toString();
//        String playerId1 = UUID.randomUUID().toString();
//        String playerId2 = UUID.randomUUID().toString();
//
//        Game game = new Game(gameId, playerId1, playerId2);
//        Player player = createPlayerAndSetAnswer(gameId, playerId1);
//
//
//        Game game1 = gameRepository.save(game);
//        Player player1 = playerRepository.save(player);