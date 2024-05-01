package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для {@link ItemController}
 */
@WebMvcTest(controllers = {ItemController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private static final String URL = "/items";

    private static final String HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemClient itemClient;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    private ItemCreateRequest createRequest;

    private CommentCreateRequest commentCreateRequest;

    private Long userId;

    private Long itemId;

    @Test
    @SneakyThrows
    void create_whenItemNameOrDescriptionOrAvailableIsNull_thenBadRequestReturned() {
        createRequest = ItemCreateRequest.builder()
                .build();
        userId = 1L;
        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenItemNameOrDescriptionIsBlank_thenBadRequestReturned() {
        createRequest = ItemCreateRequest.builder()
                .name("  ")
                .description("  ")
                .build();
        userId = 1L;
        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getByUserId_whenPaginationParamsIncorrect_thenBadRequestReturned() {
        userId = 1L;
        mockMvc.perform(get(URL)
                        .header(HEADER, userId)
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).getByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void search_whenPaginationParamsIncorrect_thenBadRequestReturned() {
        userId = 1L;
        mockMvc.perform(get(URL + "/search")
                        .header(HEADER, userId)
                        .param("text", "text")
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void createComment_whenCommentTextIsNull_thenBadRequestReturned() {
        commentCreateRequest = CommentCreateRequest.builder()
                .build();
        userId = 1L;
        itemId = 1L;

        mockMvc.perform(post(URL + "/{itemId}/comment", itemId)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).createComment(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void createComment_whenCommentTextIsBlank_thenBadRequestReturned() {
        commentCreateRequest = CommentCreateRequest.builder()
                .text("  ")
                .build();
        userId = 1L;
        itemId = 1L;

        mockMvc.perform(post(URL + "/{itemId}/comment", itemId)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).createComment(anyLong(), anyLong(), any());
    }
}