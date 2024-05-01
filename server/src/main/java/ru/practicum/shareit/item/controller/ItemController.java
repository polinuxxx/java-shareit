package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.mapper.CommentConverter;
import ru.practicum.shareit.item.mapper.ItemConverter;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentView;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.dto.ItemWithBookingView;
import ru.practicum.shareit.item.mapper.ItemModelConverter;
import ru.practicum.shareit.item.model.Item;

/**
 * Контроллер для {@link Item}.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_REQUEST_HEADER_NAME = "X-Sharer-User-Id";

    private final ItemService itemService;

    private final ItemConverter itemConverter;

    private final CommentConverter commentConverter;

    private final ItemModelConverter itemModelConverter;

    @PostMapping
    public ItemView create(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                           @RequestBody ItemCreateRequest request) {
        return itemConverter.convert(itemService.create(userId, itemConverter.convert(request)));
    }

    @PatchMapping("/{itemId}")
    public ItemView patch(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemUpdateRequest request) {
        return itemConverter.convert(itemService.patch(userId, itemId, itemConverter.convert(request)));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingView getById(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                       @PathVariable Long itemId) {
        return itemModelConverter.convert(itemService.getById(userId, itemId));
    }

    @GetMapping
    public List<ItemWithBookingView> getByUserId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return itemModelConverter.convert(itemService.getByUserId(userId, from, size));
    }

    @GetMapping("/search")
    public List<ItemView> search(@RequestParam String text,
                                 @RequestParam(defaultValue = "0") Integer from,
                                 @RequestParam(defaultValue = "10") Integer size) {
        return itemConverter.convert(itemService.search(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentView createComment(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                     @PathVariable Long itemId,
                                     @RequestBody CommentCreateRequest request) {
        return commentConverter.convert(itemService.createComment(userId, itemId, commentConverter.convert(request)));
    }
}
