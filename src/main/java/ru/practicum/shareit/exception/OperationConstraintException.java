package ru.practicum.shareit.exception;

/**
 * Исключение для запрещенных по бизнес-логике операций.
 */
public class OperationConstraintException extends RuntimeException {
    public OperationConstraintException(String message) {
        super(message);
    }
}
