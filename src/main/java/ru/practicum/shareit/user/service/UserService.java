package ru.practicum.shareit.user.service;

import java.util.List;
import ru.practicum.shareit.user.model.User;

/**
 * Сервис для {@link User}.
 */
public interface UserService {
    User create(User user);

    User patch(Long id, User user);

    User getById(Long id);

    List<User> getAll();

    void delete(Long id);
}
