package ru.practicum.shareit.common;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Аннотация для исключения из тестового покрытия генерируемых классов mapstruct.
 */
@Retention(CLASS)
public @interface GeneratedMapper {
}
