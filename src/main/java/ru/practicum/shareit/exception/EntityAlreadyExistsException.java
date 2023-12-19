package ru.practicum.shareit.exception;

/**
 * Исключение для уже созданной сущности.
 */
public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
