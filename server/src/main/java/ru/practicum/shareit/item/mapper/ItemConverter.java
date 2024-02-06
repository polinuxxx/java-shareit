package ru.practicum.shareit.item.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.AnnotateWith;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.shareit.common.GeneratedMapper;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Конвертер для {@link Item}.
 */
@Mapper(componentModel = "spring", uses = CommentConverter.class)
@AnnotateWith(GeneratedMapper.class)
public interface ItemConverter {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    Item convert(ItemCreateRequest request);

    Item convert(ItemUpdateRequest request);

    @Mapping(source = "request.id", target = "requestId")
    ItemView convert(Item item);

    List<ItemView> convert(List<Item> items);

    @AfterMapping
    default void convertItemRequest(ItemCreateRequest request, @MappingTarget Item item) {
        item.setRequest(request.getRequestId() != null ?
                ItemRequest.builder().id(request.getRequestId()).build() : null);
    }
}
