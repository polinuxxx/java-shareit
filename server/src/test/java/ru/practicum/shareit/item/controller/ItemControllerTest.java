package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.booking.internal.BookingModel;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.OperationConstraintException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.internal.CommentModel;
import ru.practicum.shareit.item.internal.ItemModel;
import ru.practicum.shareit.item.mapper.CommentConverter;
import ru.practicum.shareit.item.mapper.ItemConverter;
import ru.practicum.shareit.item.mapper.ItemModelConverter;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для {@link ItemController}
 */
@WebMvcTest(controllers = {ItemController.class, ItemConverter.class, ItemModelConverter.class, CommentConverter.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private static final String URL = "/items";

    private static final String HEADER = "X-Sharer-User-Id";

    private static final Integer DEFAULT_PAGE_START = 0;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @MockBean
    private ItemService itemService;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    private ItemCreateRequest createRequest;

    private ItemUpdateRequest updateRequest;

    private CommentCreateRequest commentCreateRequest;

    private Item item;

    private Comment comment;

    private ItemModel itemModel;

    private Long userId;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .name("thing")
                .description("description")
                .available(true)
                .request(ItemRequest.builder()
                        .id(1L)
                        .build())
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("comment")
                .author(User.builder()
                        .id(1L)
                        .name("Denis")
                        .build())
                .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        userId = 1L;
        itemModel = ItemModel.builder()
                .id(1L)
                .name("thing")
                .description("description")
                .available(true)
                .comments(List.of(CommentModel.builder()
                                .id(1L)
                                .text("comment")
                                .authorName("Denis")
                                .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                        .build()))
                .lastBooking(BookingModel.builder()
                        .id(1L)
                        .bookerId(1L)
                        .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                        .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                        .build())
                .nextBooking(BookingModel.builder()
                        .id(2L)
                        .bookerId(2L)
                        .start(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS))
                        .end(LocalDateTime.now().plusDays(4).truncatedTo(ChronoUnit.SECONDS))
                        .build())
                .build();
    }

    @Test
    @SneakyThrows
    void create_whenItemIsValid_thenItemSaved() {
        createRequest = ItemCreateRequest.builder()
                .name("thing")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
        when(itemService.create(anyLong(), any())).thenReturn(item);

        mockMvc.perform(post(URL)
                .header(HEADER, userId)
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description",
                        is(item.getDescription())))
                .andExpect(jsonPath("$.available",
                        is(item.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId",
                        is(item.getRequest().getId()), Long.class))
                .andExpect(jsonPath("$.comments", nullValue()));
        verify(itemService).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenUserNotFound_thenNotFoundReturned() {
        createRequest = ItemCreateRequest.builder()
                .name("thing")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        when(itemService.create(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(itemService).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void patch_whenItemFound_thenItemUpdated() {
        updateRequest = ItemUpdateRequest.builder()
                .name("new thing")
                .description("new description")
                .available(false)
                .build();
        item = Item.builder()
                .id(1L)
                .name("new thing")
                .description("new description")
                .available(false)
                .request(ItemRequest.builder()
                        .id(1L)
                        .build())
                .build();
        when(itemService.patch(anyLong(), anyLong(), any())).thenReturn(item);

        mockMvc.perform(patch(URL + "/{itemId}", item.getId())
                .header(HEADER, userId)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description",
                        is(item.getDescription())))
                .andExpect(jsonPath("$.available",
                        is(item.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId",
                        is(item.getRequest().getId()), Long.class))
                .andExpect(jsonPath("$.comments", nullValue()));
        verify(itemService).patch(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void patch_whenOwnerIsDifferent_thenForbiddenReturned() {
        updateRequest = ItemUpdateRequest.builder()
                .build();
        when(itemService.patch(anyLong(), anyLong(), any())).thenThrow(OperationConstraintException.class);

        mockMvc.perform(patch(URL + "/{itemId}", item.getId())
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(itemService).patch(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void patch_whenItemOrUserNotFound_thenNotFoundReturned() {
        updateRequest = ItemUpdateRequest.builder()
                .build();
        when(itemService.patch(anyLong(), anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch(URL + "/{itemId}", item.getId())
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(itemService).patch(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getById_whenItemFound_thenItemReturned() {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemModel);

        mockMvc.perform(get(URL + "/{itemId}", item.getId())
                .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(itemModel.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemModel.getName())))
                .andExpect(jsonPath("$.description",
                        is(itemModel.getDescription())))
                .andExpect(jsonPath("$.available",
                        is(itemModel.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.comments[0].id",
                        is(itemModel.getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(itemModel.getComments().get(0).getText())))
                .andExpect(jsonPath("$.comments[0].authorName",
                        is(itemModel.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created",
                        is(itemModel.getComments().get(0).getCreationDate().toString())))
                .andExpect(jsonPath("$.lastBooking.id",
                        is(itemModel.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId",
                        is(itemModel.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.start",
                        is(itemModel.getLastBooking().getStart().toString())))
                .andExpect(jsonPath("$.lastBooking.end",
                        is(itemModel.getLastBooking().getEnd().toString())))
                .andExpect(jsonPath("$.nextBooking.id",
                        is(itemModel.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId",
                        is(itemModel.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.start",
                        is(itemModel.getNextBooking().getStart().toString())))
                .andExpect(jsonPath("$.nextBooking.end",
                        is(itemModel.getNextBooking().getEnd().toString())));
        verify(itemService).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getById_whenItemNotFound_thenNotFoundReturned() {
        when(itemService.getById(anyLong(), anyLong())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(URL + "/{itemId}", item.getId())
                        .header(HEADER, userId))
                .andExpect(status().isNotFound());
        verify(itemService).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getByUserId_whenItemFoundWithoutPagination_thenItemReturned() {
        when(itemService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE)).thenReturn(List.of(itemModel));

        mockMvc.perform(get(URL)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id",
                        is(itemModel.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemModel.getName())))
                .andExpect(jsonPath("$[0].description",
                        is(itemModel.getDescription())))
                .andExpect(jsonPath("[0].available",
                        is(itemModel.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].comments[0].id",
                        is(itemModel.getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(itemModel.getComments().get(0).getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName",
                        is(itemModel.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].created",
                        is(itemModel.getComments().get(0).getCreationDate().toString())))
                .andExpect(jsonPath("$[0].lastBooking.id",
                        is(itemModel.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId",
                        is(itemModel.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.start",
                        is(itemModel.getLastBooking().getStart().toString())))
                .andExpect(jsonPath("$[0].lastBooking.end",
                        is(itemModel.getLastBooking().getEnd().toString())))
                .andExpect(jsonPath("$[0].nextBooking.id",
                        is(itemModel.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.bookerId",
                        is(itemModel.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.start",
                        is(itemModel.getNextBooking().getStart().toString())))
                .andExpect(jsonPath("$[0].nextBooking.end",
                        is(itemModel.getNextBooking().getEnd().toString())));
        verify(itemService).getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void getByUserId_whenItemFoundWithPagination_thenItemReturned() {
        when(itemService.getByUserId(userId, 1, 1)).thenReturn(List.of(itemModel));

        mockMvc.perform(get(URL)
                        .header(HEADER, userId)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id",
                        is(itemModel.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemModel.getName())))
                .andExpect(jsonPath("$[0].description",
                        is(itemModel.getDescription())))
                .andExpect(jsonPath("[0].available",
                        is(itemModel.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].comments[0].id",
                        is(itemModel.getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(itemModel.getComments().get(0).getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName",
                        is(itemModel.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].created",
                        is(itemModel.getComments().get(0).getCreationDate().toString())))
                .andExpect(jsonPath("$[0].lastBooking.id",
                        is(itemModel.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId",
                        is(itemModel.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.start",
                        is(itemModel.getLastBooking().getStart().toString())))
                .andExpect(jsonPath("$[0].lastBooking.end",
                        is(itemModel.getLastBooking().getEnd().toString())))
                .andExpect(jsonPath("$[0].nextBooking.id",
                        is(itemModel.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.bookerId",
                        is(itemModel.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.start",
                        is(itemModel.getNextBooking().getStart().toString())))
                .andExpect(jsonPath("$[0].nextBooking.end",
                        is(itemModel.getNextBooking().getEnd().toString())));
        verify(itemService).getByUserId(userId, 1, 1);
    }

    @Test
    @SneakyThrows
    void getByUserId_whenItemNotFound_thenEmptyListReturned() {
        when(itemService.getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService).getByUserId(userId, DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void search_whenItemFoundWithoutPagination_thenItemReturned() {
        when(itemService.search("text", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE)).thenReturn(List.of(item));

        mockMvc.perform(get(URL + "/search")
                .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id",
                        is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description",
                        is(item.getDescription())))
                .andExpect(jsonPath("$[0].available",
                        is(item.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId",
                        is(item.getRequest().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments", nullValue()));
        verify(itemService).search("text", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void search_whenItemFoundWithPagination_thenItemReturned() {
        when(itemService.search("text", 1, 1)).thenReturn(List.of(item));

        mockMvc.perform(get(URL + "/search")
                        .param("text", "text")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id",
                        is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description",
                        is(item.getDescription())))
                .andExpect(jsonPath("$[0].available",
                        is(item.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId",
                        is(item.getRequest().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments", nullValue()));
        verify(itemService).search("text", 1, 1);
    }

    @Test
    @SneakyThrows
    void search_whenItemNotFound_thenEmptyListReturned() {
        when(itemService.search("text", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL + "/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(itemService).search("text", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void createComment_whenCommentIsValid_thenCommentSaved() {
        commentCreateRequest = CommentCreateRequest.builder()
                .text("comment")
                .build();
        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mockMvc.perform(post(URL + "/{itemId}/comment", item.getId())
                .header(HEADER, userId)
                .content(objectMapper.writeValueAsString(commentCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName",
                        is(comment.getAuthor().getName())))
                .andExpect(jsonPath("$.created", is(comment.getCreationDate().toString())));
        verify(itemService).createComment(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void createComment_whenAuthorOrItemNotFound_thenNotFoundReturned() {
        commentCreateRequest = CommentCreateRequest.builder()
                .text("comment")
                .build();
        when(itemService.createComment(anyLong(), anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post(URL + "/{itemId}/comment", item.getId())
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(itemService).createComment(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void createComment_whenItemNotBooked_thenBadRequestReturned() {
        commentCreateRequest = CommentCreateRequest.builder()
                .text("comment")
                .build();
        when(itemService.createComment(anyLong(), anyLong(), any())).thenThrow(ValidationException.class);

        mockMvc.perform(post(URL + "/{itemId}/comment", item.getId())
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemService).createComment(anyLong(), anyLong(), any());
    }
}