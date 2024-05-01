package ru.practicum.shareit.request.dao;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Тесты для {@link ItemRequestRepository}
 */
@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

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
    void findByRequestorIdOrderByCreationDateDesc() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);

        itemRequestRepository.save(firstRequest);
        itemRequestRepository.save(secondRequest);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreationDateDesc(secondUser.getId());

        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getDescription(), equalTo(secondRequest.getDescription()));
        assertThat(requests.get(0).getRequestor().getId(), equalTo(secondRequest.getRequestor().getId()));
    }

    @Test
    void findByRequestorIdNot() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);

        itemRequestRepository.save(firstRequest);
        itemRequestRepository.save(secondRequest);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNot(secondUser.getId(), Pageable.unpaged());

        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getDescription(), equalTo(firstRequest.getDescription()));
        assertThat(requests.get(0).getRequestor().getId(), equalTo(firstRequest.getRequestor().getId()));
    }
}