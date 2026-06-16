package com.spartaclub.mini.global.exception;

public record ErrorResponseDto(int status, String error, String message) {}
