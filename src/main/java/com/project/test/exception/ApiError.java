package com.project.test.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private int responseCode;
    private String responseStatus;
    private String reason;
    @JsonProperty(value = "timestamp") // :)
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    private LocalDateTime timeStamp;
}
