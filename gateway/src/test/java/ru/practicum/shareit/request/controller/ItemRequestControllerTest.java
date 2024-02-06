package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для {@link ItemRequestController}
 */
@WebMvcTest(controllers = {ItemRequestController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private static final String URL = "/requests";

    private static final String HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestClient itemRequestClient;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    private ItemRequestCreateRequest createRequest;

    private Long userId;

    @Test
    @SneakyThrows
    void create_whenItemRequestDescriptionIsNull_thenBadRequestReturned() {
        createRequest = ItemRequestCreateRequest.builder()
                .build();
        userId = 1L;

        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemRequestClient, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenItemRequestDescriptionIsBlank_thenBadRequestReturned() {
        createRequest = ItemRequestCreateRequest.builder()
                .description("  ")
                .build();
        userId = 1L;

        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemRequestClient, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getByUserId_whenPaginationParamsIncorrect_thenBadRequestReturned() {
        userId = 1L;
        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, userId)
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verify(itemRequestClient, never()).getByUserId(anyLong(), anyInt(), anyInt());
    }
}