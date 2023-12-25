package ru.practicum.shareit.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация сервиса для {@link User}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        log.info("Добавление пользователя {}", user);
        return userRepository.create(user).toBuilder().build();
    }

    @Override
    public User patch(Long id, User user) {
        log.info("Редактирование пользователя {}", user);
        user.setId(id);
        return userRepository.patch(user).toBuilder().build();
    }

    @Override
    public User getById(Long id) {
        log.info("Получение пользователя по id = {}", id);
        return userRepository.getById(id).toBuilder().build();
    }

    @Override
    public List<User> getAll() {
        log.info("Получение всех пользователей");
        return userRepository.getAll();
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление пользователя по id = {}", id);
        userRepository.delete(id);
    }
}
