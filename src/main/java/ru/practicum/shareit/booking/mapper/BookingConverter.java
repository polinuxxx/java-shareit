package ru.practicum.shareit.booking.mapper;

import java.util.List;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingView;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.GeneratedMapper;

/**
 * Конвертер для {@link Booking}.
 */
@Mapper(componentModel = "spring")
@AnnotateWith(GeneratedMapper.class)
public interface BookingConverter {
    @Mapping(source = "itemId", target = "item.id")
    @Mapping(constant = "WAITING", target = "status")
    Booking convert(BookingCreateRequest request);

    BookingView convert(Booking booking);

    List<BookingView> convert(List<Booking> bookings);
}
