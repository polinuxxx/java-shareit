package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

/**
 * Контроллер для вещей.
 */
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID_REQUEST_HEADER_NAME = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                         @RequestBody @Valid ItemCreateRequest request) {
        log.info("Создание вещи {}", request);
        return itemClient.create(userId, request);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patch(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                        @PathVariable Long itemId,
                                        @RequestBody @Valid ItemUpdateRequest request) {
        log.info("Редактирование вещи с id = {} пользователем с id = {} по полям {}", itemId, userId, request);
        return itemClient.patch(userId, itemId, request);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                          @PathVariable Long itemId) {
        log.info("Получение вещи по id = {} пользователем с id = {}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                              @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Получение вещей по id владельца = {}", userId);
        return itemClient.getByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Поиск вещей по ключу {}", text);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentCreateRequest request) {
        log.info("Создание комментария к вещи с id = {} пользователем с id = {} с текстом {}", itemId, userId, request);
        return itemClient.createComment(userId, itemId, request);
    }
}
