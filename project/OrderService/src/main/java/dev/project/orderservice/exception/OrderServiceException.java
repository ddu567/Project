package dev.project.orderservice.exception;

import org.springframework.http.HttpStatus;

public class OrderServiceException extends RuntimeException {

    private HttpStatus status;

    public OrderServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}