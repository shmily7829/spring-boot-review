package com.mily.springbootreview.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GuessPlayerNumberData {
    private String result;
    private String winnerId;

    public boolean hasWinner() {
        return winnerId != null && !winnerId.isBlank();
    }
}
