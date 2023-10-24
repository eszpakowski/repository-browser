package com.interview.repositorybrowser.config;

import com.interview.repositorybrowser.adapter.inbound.http.dto.ResponseDTO;
import com.interview.repositorybrowser.domain.exception.BadRequestException;
import com.interview.repositorybrowser.domain.exception.RepositoryBrowserRuntimeException;
import com.interview.repositorybrowser.domain.exception.UnsupportedMediaTypeException;
import com.interview.repositorybrowser.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Custom 4xx Exceptions
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDTO> handleBadRequestException(BadRequestException ex) {
        log.warn("BadRequestException thrown:", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createBody(ex, 400));
    }

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ResponseDTO> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException ex) {
        log.warn("UnsupportedMediaTypeException thrown:", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createBody(ex, 404));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("UserNotFoundException thrown:", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createBody(ex, 404));
    }

    // Standard 4xx Exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException thrown:", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createBody(ex, 400));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("IllegalArgumentException thrown:", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createBody(ex, 400));
    }

    // Custom 5xx Exceptions
    @ExceptionHandler(RepositoryBrowserRuntimeException.class)
    public ResponseEntity<ResponseDTO> handleRepositoryBrowserRuntimeException(RepositoryBrowserRuntimeException ex) {
        log.error("RepositoryBrowserRuntimeException thrown:", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createBody(ex, 500));
    }

    // Unhandled cases
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleOtherExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createBody(ex, 500));
    }

    private static ResponseDTO createBody(Exception ex, int status) {
        return ResponseDTO.builder()
                .status(status)
                .message(ex.getMessage())
                .build();
    }
}
