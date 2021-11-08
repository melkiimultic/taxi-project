package com.example.Clientservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class OrderProcessingException extends RuntimeException{
    public OrderProcessingException(String message) {
        super(message);
    }
}
