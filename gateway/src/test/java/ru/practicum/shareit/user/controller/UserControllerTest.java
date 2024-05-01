package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для {@link UserController}.
 */
@WebMvcTest(controllers = {UserController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private static final String URL = "/users";

    @MockBean
    private UserClient userClient;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private UserCreateRequest createRequest;

    private UserUpdateRequest updateRequest;

    private Long userId;

    @Test
    @SneakyThrows
    void create_whenUserEmailIsNull_thenBadRequestReturned() {
        createRequest = UserCreateRequest.builder()
                .build();

        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userClient, never()).create(any());
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
        verify(userClient, never()).create(any());
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
        verify(userClient, never()).create(any());
    }

    @Test
    @SneakyThrows
    void patch_whenUserEmailIsInvalid_thenBadRequestReturned() {
        updateRequest = UserUpdateRequest.builder()
                .email("@incorrect.com")
                .build();
        userId = 1L;

        mockMvc.perform(patch(URL + "/{userId}", userId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).patch(anyLong(), any());
    }
}