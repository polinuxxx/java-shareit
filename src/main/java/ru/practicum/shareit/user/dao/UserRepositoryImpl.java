package ru.practicum.shareit.user.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

/**
 * Реализация ДАО для {@link User}.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    private long counter;

    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> emails = new HashSet<>();

    @Override
    public User create(User user) {
        checkEmail(user.getEmail());

        user.setId(++counter);
        users.put(user.getId(), user);
        emails.add(user.getEmail());

        return user;
    }

    @Override
    public User patch(User user) {

        User currentUser = getById(user.getId());
        if (currentUser == null) {
            throw new EntityNotFoundException("Не найден пользователь по id = " + user.getId());
        }

        if (!currentUser.getEmail().equals(user.getEmail())) {
            checkEmail(user.getEmail());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            emails.remove(currentUser.getEmail());
            emails.add(user.getEmail());
        }

        currentUser.setName(user.getName() != null && !user.getName().isBlank() ?
                user.getName() : currentUser.getName());
        currentUser.setEmail(user.getEmail() != null && !user.getEmail().isBlank() ?
                user.getEmail() : currentUser.getEmail());

        return currentUser;
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long id) {
        User user = getById(id);
        if (user != null) {
            emails.remove(user.getEmail());
            users.remove(id);
        }
    }

    @Override
    public boolean exists(Long id) {
        return users.containsKey(id);
    }

    private void checkEmail(String email) {
        if (emails.contains(email)) {
            throw new EntityAlreadyExistsException("Пользователь с адресом электронной почты " + email
                    + "уже существует");
        }
    }
}
