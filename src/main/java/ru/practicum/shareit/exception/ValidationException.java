package ru.practicum.shareit.exception;

/**
 * Исключение для валидации.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
