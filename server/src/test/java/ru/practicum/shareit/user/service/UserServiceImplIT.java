package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Интеграционные тесты для {@link UserServiceImpl}.
 */
@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIT {
    private final UserService userService;

    private User firstUser;

    private User secondUser;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        firstUser = User.builder()
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();
        secondUser = User.builder()
                .name("William Turner")
                .email("bootstrap@gmail.com")
                .build();
    }

    @Test
    void patch_whenUserFound_thenUserUpdated() {
        userService.create(firstUser);

        User updatedUser = userService.patch(firstUser.getId(), secondUser);

        assertThat(firstUser.getId(), equalTo(updatedUser.getId()));
        assertThat(secondUser.getName(), equalTo(updatedUser.getName()));
        assertThat(secondUser.getEmail(), equalTo(updatedUser.getEmail()));

    }

    @Test
    void patch_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.patch(userId, secondUser));
    }

    @Test
    void patch_whenUserEmailIsDuplicate_thenEntityAlreadyExistsExceptionThrown() {
        User savedUser = userService.create(firstUser);
        userService.create(secondUser);

        assertThrows(EntityAlreadyExistsException.class,
                () -> userService.patch(savedUser.getId(), secondUser));
    }
}