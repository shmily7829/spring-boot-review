package com.mily.springbootreview.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Response<E> {

    private E data;
    private String message;

}
