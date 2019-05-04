package com.xiaoadong.ss_use_db.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class SimpleResponse {

    private long timestamp;
    private Object content;


    public SimpleResponse(Object content) {
        this.timestamp = System.currentTimeMillis();
        this.content = content;

    }
}
