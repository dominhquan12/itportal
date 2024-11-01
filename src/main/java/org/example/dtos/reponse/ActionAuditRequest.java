package org.example.dtos.reponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionAuditRequest {
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private Object body;
    private String actionName;
    private String transactionType;
    private ActionCode actionCode;
    private String sourceClass;
    private String sourceLineNumber;
    private String sourceMethod;
    private Integer countRecord;
    private String typeDataResponse;
    private Object requestContent;
    private Timestamp startTime;
    private Timestamp endTime;
    private Long httpStatus;
    private String message;
    private String userName;

    public ActionAuditRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body, String actionName, String transactionType, ActionCode actionCode, String sourceClass,
                              String sourceLineNumber, String sourceMethod, int countRecord, String typeDataResponse, Object requestContent, Timestamp startTime, Timestamp endTime, String userName) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.body = body;
        this.actionName = actionName;
        this.transactionType = transactionType;
        this.actionCode = actionCode;
        this.sourceClass = sourceClass;
        this.sourceLineNumber = sourceLineNumber;
        this.sourceMethod = sourceMethod;
        this.countRecord = countRecord;
        this.typeDataResponse = typeDataResponse;
        this.requestContent = requestContent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userName = userName;
    }

    public static enum ActionCode {
        CREATE, READ, UPDATE, DELETE, LOGIN, DOWNLOAD, UPLOAD, IMPORT, EXPORT
    }
}