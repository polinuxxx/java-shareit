package ru.practicum.shareit.booking.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;

/**
 * Контроллер для бронирований.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID_REQUEST_HEADER_NAME = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                         @RequestBody @Valid BookingCreateRequest request) {
        log.info("Создание бронирования {}", request);
        return bookingClient.create(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patch(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        log.info("Редактирование бронирования с id = {} пользователем с id = {} по полю approved = {}",
                bookingId, userId, approved);
        return bookingClient.patch(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                          @PathVariable Long bookingId) {
        log.info("Получение бронирования с id = {} пользователем с id = {}", bookingId, userId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBookerId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Получение всех бронирований пользователя с id = {} в статусе {}", userId, state);
        return bookingClient.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Получение всех бронирований владельца вещи с id = {} в статусе {}", userId, state);
        return bookingClient.getAllByOwnerId(userId, state, from, size);
    }
}
