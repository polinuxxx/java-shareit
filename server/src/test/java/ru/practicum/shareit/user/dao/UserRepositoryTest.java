package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Тесты для {@link UserRepository}.
 */
@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final UserRepository userRepository;

    @Test
    void findByEmail() {
        User firstUser = User.builder()
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();
        User savedUser = userRepository.save(firstUser);
        User secondUser = User.builder()
                .name("WilliamTurner")
                .email("bootstrap@gmail.com")
                .build();
        userRepository.save(secondUser);

        User result = userRepository.findByEmail("sparrow@gmail.com");

        assertThat(result.getId(), equalTo(savedUser.getId()));
        assertThat(result.getName(), equalTo(savedUser.getName()));
        assertThat(result.getEmail(), equalTo(savedUser.getEmail()));
    }
}