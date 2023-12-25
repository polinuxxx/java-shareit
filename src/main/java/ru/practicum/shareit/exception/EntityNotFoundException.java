package ru.practicum.shareit.exception;

/**
 * Исключение для ненайденной сущности.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
