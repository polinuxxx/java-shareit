package ru.practicum.shareit.user.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация сервиса для {@link User}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {
        log.info("Добавление пользователя {}", user);

        return userRepository.save(user).toBuilder().build();
    }

    @Override
    @Transactional
    public User patch(Long id, User user) {
        log.info("Редактирование пользователя {}", user);

        User currentUser = getById(id);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!currentUser.getEmail().equals(user.getEmail())) {
                checkEmail(user.getEmail());
            }
            currentUser.setEmail(user.getEmail());
        }
        currentUser.setName(user.getName() != null && !user.getName().isBlank() ?
                user.getName() : currentUser.getName());

        return userRepository.save(currentUser).toBuilder().build();
    }

    @Override
    public User getById(Long id) {
        log.info("Получение пользователя по id = {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь по id = " + id));

        return user.toBuilder().build();
    }

    @Override
    public List<User> getAll() {
        log.info("Получение всех пользователей");
        return new ArrayList<>(userRepository.findAll());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаление пользователя по id = {}", id);

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    private void checkEmail(String email) {
        if (userRepository.findByEmail(email) != null) {
            throw new EntityAlreadyExistsException("Пользователь с адресом электронной почты " + email
                    + "уже существует");
        }
    }
}
