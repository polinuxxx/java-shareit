package ru.practicum.shareit.user.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.dto.UserView;
import ru.practicum.shareit.user.model.User;

/**
 * Конвертер для {@link User}.
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    User convert(UserCreateRequest request);

    User convert(UserUpdateRequest request);

    UserView convert(User user);

    List<UserView> convert(List<User> users);
}
