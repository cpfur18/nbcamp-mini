package com.spartaclub.mini.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductDuplicatedNameException.class)
    public ResponseEntity<?> handleProductDuplicatedNameException(
            ProductDuplicatedNameException ex) {
        log.warn("Product Duplicated Name: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.name(), "중복된 상품 이름 입니다."));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFoundException(
            ProductNotFoundException ex) {
        log.warn("Product not found: {}", ex.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.name(), "상품을 찾을 수 없습니다."));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleOrderNotFoundException(
            OrderNotFoundException ex) {
        log.warn("Order not found: {}", ex.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.name(), "주문을 찾을 수 없습니다."));
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<ErrorResponseDto> handleNotEnoughStockException(
            NotEnoughStockException ex) {
        log.warn("Not enough stock: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.name(), "재고가 부족합니다."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        log.warn("Data Integrity Violation: {}", ex.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.name(), "이미 존재하는 데이터 입니다."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.warn("Message not readable: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.name(), "잘못된 요청입니다."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        log.warn("Argument type mismatch: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.name(), "잘못된 요청입니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidException(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        log.warn("Validation error: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(new ErrorResponseDto(status.value(), status.name(), message));
    }

    // TODO : 페이징 오류 시 예외처리 추가해야함.
}
