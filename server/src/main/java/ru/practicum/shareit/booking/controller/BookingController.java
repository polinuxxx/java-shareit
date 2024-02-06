package ru.practicum.shareit.booking.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingView;
import ru.practicum.shareit.booking.mapper.BookingConverter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

/**
 * Контроллер для {@link Booking}.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID_REQUEST_HEADER_NAME = "X-Sharer-User-Id";

    private final BookingService bookingService;

    private final BookingConverter bookingConverter;

    @PostMapping
    public BookingView create(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                              @RequestBody BookingCreateRequest request) {
        return bookingConverter.convert(bookingService.create(userId, bookingConverter.convert(request)));
    }

    @PatchMapping("/{bookingId}")
    public BookingView patch(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved) {
        return bookingConverter.convert(bookingService.patch(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingView getById(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                               @PathVariable Long bookingId) {
        return bookingConverter.convert(bookingService.getById(userId, bookingId));
    }

    @GetMapping
    public List<BookingView> getAllByBookerId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return bookingConverter.convert(bookingService.getAllByBookerId(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingView> getAllByOwnerId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return bookingConverter.convert(bookingService.getAllByOwnerId(userId, state, from, size));
    }
}
