package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

/**
 * Контроллер для {@link Item}.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemView create(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody @Valid ItemCreateRequest request) {
        return ItemMapper.toItemView(itemService.create(userId, ItemMapper.toItem(request)));
    }

    @PatchMapping("/{itemId}")
    public ItemView patch(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody @Valid ItemUpdateRequest request) {
        return ItemMapper.toItemView(itemService.patch(userId, itemId, ItemMapper.toItem(request)));
    }

    @GetMapping("/{itemId}")
    public ItemView getById(@PathVariable Long itemId) {
        return ItemMapper.toItemView(itemService.getById(itemId));
    }

    @GetMapping
    public List<ItemView> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemViewList(itemService.getByUserId(userId));
    }

    @GetMapping("/search")
    public List<ItemView> search(@RequestParam String text) {
        return ItemMapper.toItemViewList(itemService.search(text));
    }
}
