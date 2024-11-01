package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.dtos.reponse.ValidateFileResponse;
import org.example.dtos.request.CdeRequest;
import org.example.services.CdeService;
import org.example.utils.ResponseBase;
import org.example.utils.ResponseBase.ResponseType;
import org.example.utils.common.Constants;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(value = "api/v1/cdes")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CdeController {

    CdeService cdeService;

    @GetMapping("/download-template")
    public ResponseEntity<ByteArrayResource> downloadTemplate() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=cde_template_data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(cdeService.downloadTemplate());
    }

    @PostMapping("/validate") //validate file import
    public ResponseEntity<ResponseBase> validateFileImport(@RequestPart(value = "file") MultipartFile multipartFile, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        ValidateFileResponse validateFileResponse = new ValidateFileResponse();
        AtomicInteger typeResponse = new AtomicInteger(Constants.FILE_IMPORT_SUCCESS);
        AtomicBoolean checkData = new AtomicBoolean(false);
        cdeService.validateFile(multipartFile, validateFileResponse, typeResponse, checkData, httpServletRequest, httpServletResponse);
        ResponseBase responseBase = null;
        if (typeResponse.get() == Constants.FILE_IMPORT_INVALID) {
            responseBase = new ResponseBase(new Object[]{ResponseType.FAIL}, ResponseType.FAIL, Constants.FILE_INVALID);
            return new ResponseEntity<>(responseBase, HttpStatus.BAD_REQUEST);
        } else if (typeResponse.get() == Constants.FILE_IMPORT_SUCCESS) {
            responseBase = new ResponseBase(new Object[]{validateFileResponse}, ResponseType.SUCCESS, "");
            return new ResponseEntity<>(responseBase, HttpStatus.OK);
        } else if (typeResponse.get() == Constants.FILE_IMPORT_ERROR) {
            responseBase = new ResponseBase(new Object[]{validateFileResponse}, ResponseType.FAIL, "");
            return new ResponseEntity<>(responseBase, HttpStatus.EXPECTATION_FAILED);
        } else {
            responseBase = new ResponseBase(new Object[]{validateFileResponse}, ResponseType.FAIL, "");
            return new ResponseEntity<>(responseBase, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/import")
    public ResponseEntity<ByteArrayResource> importFile(@RequestParam(name = "name") String name, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        ByteArrayResource byteArrayResource = cdeService.importFile(name, httpServletRequest, httpServletResponse);
        if (byteArrayResource != null) {
            if (byteArrayResource.equals(new ByteArrayResource(Constants.FILE_INVALID_IMPORT))) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(byteArrayResource, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("download/file-response") //download file ket qua
    public ResponseEntity<ByteArrayResource> getFile(@RequestParam(name = "name") String name, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return ResponseEntity.ok(cdeService.getFile(name, httpServletRequest, httpServletResponse));
    }

    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> export(@RequestBody CdeRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=cde_export_data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(cdeService.export(request, httpServletRequest, httpServletResponse));
    }
}
