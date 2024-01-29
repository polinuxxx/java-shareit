package ru.practicum.shareit.item.mapper;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.common.GeneratedMapper;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentView;
import ru.practicum.shareit.item.model.Comment;

/**
 * Конвертер для {@link Comment}.
 */
@Mapper(componentModel = "spring", imports = java.time.LocalDateTime.class)
@AnnotateWith(GeneratedMapper.class)
public interface CommentConverter {

    @Mapping(expression = "java(LocalDateTime.now())", target = "creationDate")
    Comment convert(CommentCreateRequest request);

    @Mapping(source = "author.name", target = "authorName")
    CommentView convert(Comment comment);
}
