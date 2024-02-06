package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.internal.ItemRequestModel;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Интеграционные тесты для {@link ItemRequestServiceImpl}
 */
@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIT {
    private final ItemRequestService itemRequestService;

    private final UserService userService;

    private final ItemService itemService;

    private ItemRequest itemRequest;

    private User user;

    private Item item;

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
                .request(itemRequest)
                .build();
    }

    @Test
    void getById_whenItemRequestFound_thenItemRequestReturned() {
        userService.create(user);
        itemRequestService.create(user.getId(), itemRequest);
        itemService.create(user.getId(), item);

        ItemRequestModel result = itemRequestService.getById(user.getId(), itemRequest.getId());

        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getId(), equalTo(itemRequest.getId()));
        assertThat(result.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(result.getCreationDate(), equalTo(itemRequest.getCreationDate()));
        assertThat(result.getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(result.getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(result.getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(result.getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getItems().get(0).getRequestId(), equalTo(item.getRequest().getId()));
    }

    @Test
    void getById_whenItemRequestNotFound_thenEntityNotFoundExceptionThrown() {
        userService.create(user);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getById(user.getId(), user.getId()));
    }

    @Test
    void getById_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getById(userId, userId));
    }

    @Test
    void getById_whenItemRequestFoundAndItemsAreEmpty_thenItemRequestWithEmptyItemsReturned() {
        userService.create(user);
        itemRequestService.create(user.getId(), itemRequest);

        ItemRequestModel result = itemRequestService.getById(user.getId(), itemRequest.getId());

        assertThat(result.getItems(), hasSize(0));
        assertThat(result.getId(), equalTo(itemRequest.getId()));
        assertThat(result.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(result.getCreationDate(), equalTo(itemRequest.getCreationDate()));
    }
}