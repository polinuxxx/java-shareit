package ru.practicum.shareit.user.dao;

import java.util.List;
import ru.practicum.shareit.user.model.User;

/**
 * ДАО для {@link User}.
 */
public interface UserRepository {
    User create(User user);

    User patch(User user);

    User getById(Long id);

    List<User> getAll();

    void delete(Long id);

    boolean exists(Long id);
}
