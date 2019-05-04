package com.xiaoadong.ss_use_db.vo;

import lombok.Data;

@Data
public class SimpleExceptionResponse {
    private long timestamp;
    private String exception;
    private String message;

    public SimpleExceptionResponse(Exception exception) {
        this.exception = exception.getClass().getName();
        this.message = exception.getMessage();
        this.timestamp = System.currentTimeMillis();
    }
}
