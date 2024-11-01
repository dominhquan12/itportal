package org.example.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBase {
    private Object[] results;
    private ResponseType responseType = ResponseType.SUCCESS;
    private String errorCode;
    private String message;
    private String errorKey;
    public static enum ResponseType {
        SUCCESS, FAIL
    }

    public ResponseBase(Object[] results, ResponseType responseType, String message) {
        this.results = results;
        this.responseType = responseType;
        this.message = message;
    }
}
