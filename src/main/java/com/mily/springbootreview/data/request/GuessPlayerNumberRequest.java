package com.mily.springbootreview.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuessPlayerNumberRequest {
    private String guesserId;
    private String number;
}
