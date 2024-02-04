package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.internal.ItemRequestModel;
import ru.practicum.shareit.request.mapper.ItemRequestConverter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для {@link ItemRequestController}
 */
@WebMvcTest(controllers = {ItemRequestController.class, ItemRequestConverter.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private static final String URL = "/requests";

    private static final String HEADER = "X-Sharer-User-Id";

    private static final Integer DEFAULT_PAGE_START = 0;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    private ItemRequestCreateRequest createRequest;

    private ItemRequest itemRequest;

    private ItemRequestModel requestModel;

    private Long userId;

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        userId = 1L;
        requestModel = ItemRequestModel.builder()
                .id(1L)
                .description("description")
                .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .items(List.of(ItemRequestModel.ItemModel.builder()
                        .id(1L)
                        .name("thing")
                        .description("description")
                        .available(true)
                        .requestId(1L)
                        .build()))
                .build();
    }

    @Test
    @SneakyThrows
    void create_whenItemRequestIsValid_thenItemRequestSaved() {
        createRequest = ItemRequestCreateRequest.builder()
                .description("description")
                .build();

        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequest);

        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequest.getCreationDate().toString())))
                .andExpect(jsonPath("$.items", nullValue()));
        verify(itemRequestService).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenItemRequestDescriptionIsNull_thenBadRequestReturned() {
        createRequest = ItemRequestCreateRequest.builder()
                .build();

        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenItemRequestDescriptionIsBlank_thenBadRequestReturned() {
        createRequest = ItemRequestCreateRequest.builder()
                .description("  ")
                .build();

        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenUserNotFound_thenNotFoundReturned() {
        createRequest = ItemRequestCreateRequest.builder()
                .description("description")
                .build();

        when(itemRequestService.create(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(itemRequestService).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getByRequestorId_whenRequestFound_thenRequestReturned() {
        when(itemRequestService.getByRequestorId(userId)).thenReturn(List.of(requestModel));

        mockMvc.perform(get(URL)
                .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestModel.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestModel.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestModel.getCreationDate().toString())))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(requestModel.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(requestModel.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(requestModel.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(requestModel.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(requestModel.getItems().get(0).getRequestId()), Long.class));
        verify(itemRequestService).getByRequestorId(userId);
    }

    @Test
    @SneakyThrows
    void getByRequestorId_whenRequestNotFound_thenEmptyListReturned() {
        when(itemRequestService.getByRequestorId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL)
                .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(itemRequestService).getByRequestorId(userId);
    }

    @Test
    @SneakyThrows
    void getByRequestorId_whenUserNotFound_thenNotFoundRetunred() {
        when(itemRequestService.getByRequestorId(userId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(URL)
                .header(HEADER, userId))
                .andExpect(status().isNotFound());
        verify(itemRequestService).getByRequestorId(userId);
    }

    @Test
    @SneakyThrows
    void getByUserId_whenRequestFoundWithoutPagination_thenRequestReturned() {
        when(itemRequestService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenReturn(List.of(requestModel));

        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestModel.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestModel.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestModel.getCreationDate().toString())))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(requestModel.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(requestModel.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(requestModel.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(requestModel.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(requestModel.getItems().get(0).getRequestId()), Long.class));
        verify(itemRequestService).getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void getByUserId_whenPaginationParamsIncorrect_thenBadRequestReturned() {
        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, userId)
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).getByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getByUserId_whenRequestFoundWithPagination_thenRequestReturned() {
        when(itemRequestService.getByUserId(userId, 1, 1)).thenReturn(List.of(requestModel));

        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, userId)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestModel.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestModel.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestModel.getCreationDate().toString())))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(requestModel.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(requestModel.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(requestModel.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(requestModel.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].requestId",
                        is(requestModel.getItems().get(0).getRequestId()), Long.class));
        verify(itemRequestService).getByUserId(userId, 1, 1);
    }

    @Test
    @SneakyThrows
    void getByUserId_whenRequestNotFound_thenEmptyListReturned() {
        when(itemRequestService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemRequestService).getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void getByUserId_whenUserNotFound_thenNotFoundReturned() {
        when(itemRequestService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, userId))
                .andExpect(status().isNotFound());
        verify(itemRequestService).getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void getById_whenRequestFound_thenRequestReturned() {
        when(itemRequestService.getById(userId, userId)).thenReturn(requestModel);

        mockMvc.perform(get(URL + "/{requestId}", userId)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestModel.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestModel.getDescription())))
                .andExpect(jsonPath("$.created", is(requestModel.getCreationDate().toString())))
                .andExpect(jsonPath("$.items[0].id",
                        is(requestModel.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(requestModel.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description",
                        is(requestModel.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available",
                        is(requestModel.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.items[0].requestId",
                        is(requestModel.getItems().get(0).getRequestId()), Long.class));
        verify(itemRequestService).getById(userId, userId);
    }

    @Test
    @SneakyThrows
    void getById_whenRequestOrUserNotFound_thenNotFoundReturned() {
        when(itemRequestService.getById(userId, userId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(URL + "/{requestId}", userId)
                        .header(HEADER, userId))
                .andExpect(status().isNotFound());
        verify(itemRequestService).getById(userId, userId);
    }
}