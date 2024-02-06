package ru.practicum.shareit.user.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.dto.UserView;
import ru.practicum.shareit.user.mapper.UserConverter;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

/**
 * Контроллер для {@link User}.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final UserConverter userConverter;

    @PostMapping
    public UserView create(@RequestBody UserCreateRequest request) {
        return userConverter.convert(userService.create(userConverter.convert(request)));
    }

    @PatchMapping("/{userId}")
    public UserView patch(@PathVariable Long userId,
                          @RequestBody UserUpdateRequest request) {
        return userConverter.convert(userService.patch(userId, userConverter.convert(request)));
    }

    @GetMapping("/{userId}")
    public UserView getById(@PathVariable Long userId) {
        return userConverter.convert(userService.getById(userId));
    }

    @GetMapping
    public List<UserView> getAll() {
        return userConverter.convert(userService.getAll());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
