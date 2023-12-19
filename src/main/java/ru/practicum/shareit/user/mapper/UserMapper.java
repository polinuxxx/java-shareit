package ru.practicum.shareit.user.mapper;

import java.util.List;
import java.util.stream.Collectors;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.dto.UserView;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер для {@link User}.
 */
public class UserMapper {
    public static UserView toUserView(User user) {
        return new UserView(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static List<UserView> toUserViewList(List<User> users) {
        return users.stream().map(UserMapper::toUserView).collect(Collectors.toList());
    }

    public static User toUser(UserCreateRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public static User toUser(UserUpdateRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }
}
