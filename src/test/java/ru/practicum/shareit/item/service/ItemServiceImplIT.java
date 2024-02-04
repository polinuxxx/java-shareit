package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Интеграционные тесты для {@link ItemServiceImpl}
 */
@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIT {
    private final ItemService itemService;

    private final UserService userService;

    private final ItemRequestService itemRequestService;

    private Item item;

    private User user;

    private ItemRequest itemRequest;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = User.builder()
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();
        itemRequest = ItemRequest.builder()
                .description("description")
                .creationDate(LocalDateTime.now())
                .build();
        item = Item.builder()
                .name("thing")
                .description("description")
                .available(true)
                .build();
    }

    @Test
    void create_whenItemIsValidWithRequest_thenItemSaved() {
        userService.create(user);
        itemRequestService.create(user.getId(), itemRequest);
        item.setRequest(itemRequest);
        Item savedItem = itemService.create(user.getId(), item);

        assertThat(savedItem.getId(), equalTo(item.getId()));
        assertThat(savedItem.getName(), equalTo(item.getName()));
        assertThat(savedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(savedItem.getOwner().getId(), equalTo(item.getOwner().getId()));
        assertThat(savedItem.getRequest().getId(), equalTo(item.getRequest().getId()));
    }

    @Test
    void create_whenItemIsValidWithoutRequest_thenItemSaved() {
        userService.create(user);
        Item savedItem = itemService.create(user.getId(), item);

        assertThat(savedItem.getId(), equalTo(item.getId()));
        assertThat(savedItem.getName(), equalTo(item.getName()));
        assertThat(savedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(savedItem.getOwner().getId(), equalTo(item.getOwner().getId()));
        assertThat(savedItem.getRequest(), nullValue());
    }

    @Test
    void create_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        assertThrows(EntityNotFoundException.class, () -> itemService.create(userId, item));
    }

    @Test
    void create_whenItemRequestNotFound_thenEntityNotFoundExceptionThrown() {
        userService.create(user);
        item.setRequest(itemRequest);

        assertThrows(EntityNotFoundException.class, () -> itemService.create(userId, item));
    }
}