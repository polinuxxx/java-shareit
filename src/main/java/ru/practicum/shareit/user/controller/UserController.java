package ru.practicum.shareit.user.controller;

import java.util.List;
import javax.validation.Valid;
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
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

/**
 * Контроллер для {@link User}.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserView create(@RequestBody @Valid UserCreateRequest request) {
        return UserMapper.toUserView(userService.create(UserMapper.toUser(request)));
    }

    @PatchMapping("/{userId}")
    public UserView patch(@PathVariable Long userId,
                          @RequestBody @Valid UserUpdateRequest request) {
        return UserMapper.toUserView(userService.patch(userId, UserMapper.toUser(request)));
    }

    @GetMapping("/{userId}")
    public UserView getById(@PathVariable Long userId) {
        return UserMapper.toUserView(userService.getById(userId));
    }

    @GetMapping
    public List<UserView> getAll() {
        return UserMapper.toUserViewList(userService.getAll());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
