package ru.practicum.shareit.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Описание ошибки.
 */
@RequiredArgsConstructor
@Getter
public class ErrorResponse {
    private final String error;
}
