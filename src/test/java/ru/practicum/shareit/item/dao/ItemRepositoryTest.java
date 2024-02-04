package ru.practicum.shareit.item.dao;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Тесты для {@link ItemRepository}
 */
@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    private Item firstItem;

    private Item secondItem;

    private User firstUser;

    private User secondUser;

    private ItemRequest firstRequest;

    private ItemRequest secondRequest;

    @BeforeEach
    void setUp() {
        firstUser = User.builder()
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();
        secondUser = User.builder()
                .name("WilliamTurner")
                .email("bootstrap@gmail.com")
                .build();
        firstItem = Item.builder()
                .name("item 1")
                .description("description")
                .available(true)
                .build();
        secondItem = Item.builder()
                .name("item 2")
                .description("something")
                .available(true)
                .build();
        firstRequest = ItemRequest.builder()
                .description("description 1")
                .requestor(firstUser)
                .build();
        secondRequest = ItemRequest.builder()
                .description("description 2")
                .requestor(secondUser)
                .build();
    }

    @Test
    void findByOwnerIdOrderByIdAsc() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);

        firstItem.setOwner(firstUser);
        itemRepository.save(firstItem);

        secondItem.setOwner(secondUser);
        itemRepository.save(secondItem);

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(firstUser.getId(), Pageable.unpaged());

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getName(), equalTo(firstItem.getName()));
        assertThat(items.get(0).getDescription(), equalTo(firstItem.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(firstItem.getAvailable()));
    }

    @ParameterizedTest
    @CsvSource({
            "descr, 1",
            "it, 2",
            "nothing, 0",
    })
    void search(String text, Integer resultSize) {
        itemRepository.save(firstItem);
        itemRepository.save(secondItem);

        List<Item> items = itemRepository.search(text, Pageable.unpaged());
        assertThat(resultSize, equalTo(items.size()));
    }

    @Test
    void findByRequestId() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);

        itemRequestRepository.save(firstRequest);
        itemRequestRepository.save(secondRequest);

        firstItem.setRequest(firstRequest);
        itemRepository.save(firstItem);

        secondItem.setRequest(secondRequest);
        itemRepository.save(secondItem);

        List<Item> items = itemRepository.findByRequestId(firstRequest.getId());

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getName(), equalTo(firstItem.getName()));
        assertThat(items.get(0).getDescription(), equalTo(firstItem.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(firstItem.getAvailable()));
        assertThat(items.get(0).getRequest().getId(), equalTo(firstItem.getRequest().getId()));
    }

    @Test
    void findByRequestIdIn() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);

        itemRequestRepository.save(firstRequest);
        itemRequestRepository.save(secondRequest);

        firstItem.setRequest(firstRequest);
        itemRepository.save(firstItem);

        secondItem.setRequest(secondRequest);
        itemRepository.save(secondItem);

        List<Item> items = itemRepository.findByRequestIdIn(List.of(firstRequest.getId(), secondRequest.getId()));

        assertThat(items, hasSize(2));
        assertThat(items.get(0).getName(), equalTo(firstItem.getName()));
        assertThat(items.get(0).getDescription(), equalTo(firstItem.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(firstItem.getAvailable()));
        assertThat(items.get(0).getRequest().getId(), equalTo(firstItem.getRequest().getId()));
        assertThat(items.get(1).getName(), equalTo(secondItem.getName()));
        assertThat(items.get(1).getDescription(), equalTo(secondItem.getDescription()));
        assertThat(items.get(1).getAvailable(), equalTo(secondItem.getAvailable()));
        assertThat(items.get(1).getRequest().getId(), equalTo(secondItem.getRequest().getId()));
    }
}