package org.example.dtos.reponse;

import lombok.Data;

@Data
public class ValidateFileResponse {
    private Integer numberExist = 0;
    private String fileResponse;
    private Integer statusCode;
}