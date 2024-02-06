package ru.practicum.shareit.request.mapper;

import java.util.List;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.common.GeneratedMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestView;
import ru.practicum.shareit.request.internal.ItemRequestModel;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Конвертер для {@link ItemRequest}.
 */
@Mapper(componentModel = "spring", imports = java.time.LocalDateTime.class)
@AnnotateWith(GeneratedMapper.class)
public interface ItemRequestConverter {
    @Mapping(expression = "java(LocalDateTime.now())", target = "creationDate")
    ItemRequest convert(ItemRequestCreateRequest request);

    ItemRequestView convert(ItemRequest itemRequest);

    @Mapping(source = "request.id", target = "requestId")
    ItemRequestModel.ItemModel convert(Item item);

    List<ItemRequestModel.ItemModel> convertItems(List<Item> items);

    @Mapping(source = "itemRequest.id", target = "id")
    @Mapping(source = "itemRequest.description", target = "description")
    @Mapping(source = "itemRequest.creationDate", target = "creationDate")
    ItemRequestModel convert(ItemRequest itemRequest, List<Item> items);

    ItemRequestView.ItemView convert(ItemRequestModel.ItemModel itemModel);

    List<ItemRequestView.ItemView> convertItemModels(List<ItemRequestModel.ItemModel> itemModels);

    ItemRequestView convert(ItemRequestModel itemRequestModel);

    List<ItemRequestView> convert(List<ItemRequestModel> itemRequests);
}
