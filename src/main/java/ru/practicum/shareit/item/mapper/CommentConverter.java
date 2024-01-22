package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentView;
import ru.practicum.shareit.item.model.Comment;

/**
 * Конвертер для {@link Comment}.
 */
@Mapper(componentModel = "spring", imports = java.time.LocalDateTime.class)
public interface CommentConverter {

    Comment convert(CommentCreateRequest request);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(expression = "java(LocalDateTime.now())", target = "created")
    CommentView convert(Comment comment);
}
