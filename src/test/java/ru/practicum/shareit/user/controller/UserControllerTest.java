package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserConverter;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для {@link UserController}.
 */
@WebMvcTest(controllers = {UserController.class, UserConverter.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private static final String URL = "/users";

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    private UserCreateRequest createRequest;

    private UserUpdateRequest updateRequest;

    private User user;

    private Long userId;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();
        userId = 1L;
    }

    @Test
    @SneakyThrows
    void create_whenUserIsValid_thenUserSaved() {
        createRequest = UserCreateRequest.builder()
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();

        when(userService.create(any()))
                .thenReturn(user);

        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService).create(any());
    }

    @Test
    @SneakyThrows
    void create_whenUserEmailIsNull_thenBadRequestReturned() {
        createRequest = UserCreateRequest.builder()
                .build();

        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any());
    }

    @Test
    @SneakyThrows
    void create_whenUserEmailIsBlank_thenBadRequestReturned() {
        createRequest = UserCreateRequest.builder()
                .email("   ")
                .build();
        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any());
    }

    @Test
    @SneakyThrows
    void create_whenUserEmailIsInvalid_thenBadRequestReturned() {
        createRequest = UserCreateRequest.builder()
                .email("@incorrect.com")
                .build();
        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).create(any());
    }

    @Test
    @SneakyThrows
    void create_whenUserEmailIsDuplicate_thenInternalServerErrorReturned() {
        createRequest = UserCreateRequest.builder()
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();

        when(userService.create(any()))
                .thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(userService).create(any());
    }

    @Test
    @SneakyThrows
    void patch_whenUserIsValid_thenUserUpdated() {
        updateRequest = UserUpdateRequest.builder()
                .name("William Turner")
                .email("bootstrap@gmail.com")
                .build();
        user = User.builder()
                .id(1L)
                .name("William Turner")
                .email("bootstrap@gmail.com")
                .build();
        when(userService.patch(anyLong(), any()))
                .thenReturn(user);

        mockMvc.perform(patch(URL + "/{userId}", userId)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateRequest.getName())))
                .andExpect(jsonPath("$.email", is(updateRequest.getEmail())));
        verify(userService).patch(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void patch_whenUserEmailIsInvalid_thenBadRequestReturned() {
        updateRequest = UserUpdateRequest.builder()
                .email("@incorrect.com")
                .build();

        mockMvc.perform(patch(URL + "/{userId}", userId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).patch(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void patch_whenUserEmailIsDuplicate_thenConflictReturned() {
        updateRequest = UserUpdateRequest.builder()
                .build();

        when(userService.patch(anyLong(), any())).thenThrow(EntityAlreadyExistsException.class);

        mockMvc.perform(patch(URL + "/{userId}", userId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(userService).patch(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getById_whenUserFound_thenUserReturned() {
        when(userService.getById(user.getId()))
                .thenReturn(user);
        mockMvc.perform(get(URL + "/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
        verify(userService).getById(user.getId());
    }

    @Test
    @SneakyThrows
    void getById_whenUserNotFound_thenNotFoundReturned() {
        when(userService.getById(userId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(URL + "/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userService).getById(userId);
    }

    @Test
    @SneakyThrows
    void getAll_whenUserPresent_thenUserReturned() {
        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));
        verify(userService).getAll();
    }

    @Test
    @SneakyThrows
    void getAll_whenUserNotPresent_thenEmptyListReturned() {
        when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(userService).getAll();
    }

    @Test
    @SneakyThrows
    void delete_whenUserFound_thenUserDeleted() {
        mockMvc.perform(delete(URL + "/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService).delete(userId);
    }
}