package ru.practicum.shareit.item.mapper;

import java.util.List;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.internal.BookingModel;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.GeneratedMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingView;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.CommentView;
import ru.practicum.shareit.item.internal.CommentModel;
import ru.practicum.shareit.item.internal.ItemModel;
import ru.practicum.shareit.item.model.Comment;

/**
 * Конвертер ответа сервера для {@link Item} (промежуточный слой).
 */
@Mapper(componentModel = "spring", imports = java.time.LocalDateTime.class)
@AnnotateWith(GeneratedMapper.class)
public interface ItemModelConverter {

    CommentView convert(CommentModel commentModel);

    List<CommentView> convertCommentModels(List<CommentModel> commentModels);

    @Mapping(source = "author.name", target = "authorName")
    CommentModel convert(Comment comment);

    List<CommentModel> convertComments(List<Comment> comments);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    ItemModel convert(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments);

    @Mapping(source = "id", target = "item.id")
    @Mapping(source = "name", target = "item.name")
    @Mapping(source = "description", target = "item.description")
    @Mapping(source = "available", target = "item.available")
    @Mapping(source = "comments", target = "item.comments")
    ItemWithBookingView convert(ItemModel itemModel);

    List<ItemWithBookingView> convert(List<ItemModel> itemModels);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingModel convert(Booking booking);
}
