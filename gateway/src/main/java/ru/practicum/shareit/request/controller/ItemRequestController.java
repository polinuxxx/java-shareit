package ru.practicum.shareit.request.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;

/**
 * Контроллер для запросов на вещи.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final String USER_ID_REQUEST_HEADER_NAME = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                         @RequestBody @Valid ItemRequestCreateRequest request) {
        log.info("Создание запроса на вещь {}", request);
        return itemRequestClient.create(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequestorId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId) {
        log.info("Получение всех запросов на вещи пользователя по id = {}", userId);
        return itemRequestClient.getByRequestorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getByUserId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Получение всех запросов на вещи");
        return itemRequestClient.getByUserId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                   @PathVariable Long requestId) {
        log.info("Получение запроса на вещь по id запроса = {}", requestId);
        return itemRequestClient.getById(userId, requestId);
    }
}
