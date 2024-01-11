package ru.practicum.shareit.booking.controller;

import java.util.List;
import javax.validation.Valid;
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
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;

/**
 * Контроллер для {@link Booking}.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    private final BookingConverter bookingConverter;

    @PostMapping
    public BookingView create(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingCreateRequest request) {
        return bookingConverter.convert(bookingService.create(userId, bookingConverter.convert(request)));
    }

    @PatchMapping("/{bookingId}")
    public BookingView patch(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved) {
        return bookingConverter.convert(bookingService.patch(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingView getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long bookingId) {
        return bookingConverter.convert(bookingService.getById(userId, bookingId));
    }

    @GetMapping
    public List<BookingView> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        return bookingConverter.convert(bookingService.getAllByBookerId(userId, convertStringToState(state)));
    }

    @GetMapping("/owner")
    public List<BookingView> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingConverter.convert(bookingService.getAllByOwnerId(userId, convertStringToState(state)));
    }

    private BookingState convertStringToState(String stringState) {
        try {
            return BookingState.valueOf(stringState);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
