package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dtos.reponse.ValidateFileResponse;
import org.example.dtos.request.CdeRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface CdeService {
    ByteArrayResource downloadTemplate();
    void validateFile (MultipartFile file, ValidateFileResponse validateFileResponse, AtomicInteger typeResponse, AtomicBoolean checkData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
    ByteArrayResource importFile(String name, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
    ByteArrayResource getFile(String name, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException;
    ByteArrayResource export(CdeRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws JsonProcessingException;
}
