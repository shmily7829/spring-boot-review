package com.mily.springbootreview.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GameData {
    private String gameId;
    private String player1Id;
    private String player2Id;

    public GameData(String gameId, String player1Id, String player2Id) {
        this.gameId = gameId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }
}
