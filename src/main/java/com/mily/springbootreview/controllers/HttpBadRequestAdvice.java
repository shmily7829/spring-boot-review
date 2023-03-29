package com.mily.springbootreview.controllers;

import com.mily.springbootreview.data.response.Response;
import com.mily.springbootreview.exceptions.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = GameController.class)
public class HttpBadRequestAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException.class)
    public Response<?> handleGameServiceException(ServiceException ex) {

        Response<?> response = new Response<>();
        response.setMessage(ex.getMessage());
        return response;
    }
}
