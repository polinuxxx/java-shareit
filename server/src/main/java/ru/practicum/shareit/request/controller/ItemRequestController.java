package ru.practicum.shareit.request.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestView;
import ru.practicum.shareit.request.mapper.ItemRequestConverter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

/**
 * Контроллер для {@link ItemRequest}.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String USER_ID_REQUEST_HEADER_NAME = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    private final ItemRequestConverter itemRequestConverter;

    @PostMapping
    public ItemRequestView create(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                  @RequestBody ItemRequestCreateRequest request) {
        return itemRequestConverter.convert(itemRequestService.create(userId, itemRequestConverter.convert(request)));
    }

    @GetMapping
    public List<ItemRequestView> getByRequestorId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId) {
        return itemRequestConverter.convert(itemRequestService.getByRequestorId(userId));
    }

    @GetMapping("/all")
    public List<ItemRequestView> getByUserId(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestConverter.convert(itemRequestService.getByUserId(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestView getById(@RequestHeader(USER_ID_REQUEST_HEADER_NAME) Long userId,
                                   @PathVariable Long requestId) {
        return itemRequestConverter.convert(itemRequestService.getById(userId, requestId));
    }
}
