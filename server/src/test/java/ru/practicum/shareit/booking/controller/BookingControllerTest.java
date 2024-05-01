package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.mapper.BookingConverter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для {@link BookingController}
 */
@WebMvcTest(controllers = {BookingController.class, BookingConverter.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private static final String URL = "/bookings";

    private static final String HEADER = "X-Sharer-User-Id";

    private static final Integer DEFAULT_PAGE_START = 0;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    private BookingCreateRequest createRequest;

    private Booking booking;

    private Long userId;

    @BeforeEach
    void setUp() {
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .status(BookingStatus.WAITING)
                .booker(User.builder()
                        .id(1L)
                        .build())
                .item(Item.builder()
                        .id(1L)
                        .name("thing")
                        .build())
                .build();
        userId = 1L;
    }

    @Test
    @SneakyThrows
    void create_whenBookingIsValid_thenBookingSaved() {
        createRequest = BookingCreateRequest.builder()
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .itemId(1L)
                .build();
        when(bookingService.create(anyLong(), any())).thenReturn(booking);

        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())));
        verify(bookingService).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenUserOrItemNotFound_thenNotFoundReturned() {
        createRequest = BookingCreateRequest.builder()
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .itemId(1L)
                .build();
        when(bookingService.create(anyLong(), any())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(bookingService).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenItemNotAvailable_thenBadRequestReturned() {
        createRequest = BookingCreateRequest.builder()
                .start(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .itemId(1L)
                .build();
        when(bookingService.create(anyLong(), any())).thenThrow(ValidationException.class);
        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void patch_whenBookingIsValid_thenBookingUpdated() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.patch(userId, booking.getId(), true)).thenReturn(booking);
        mockMvc.perform(patch(URL + "/{bookingId}", booking.getId())
                .header(HEADER, userId)
                .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())));
        verify(bookingService).patch(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void patch_whenUserOrBookingNotFound_thenNotFoundReturned() {
        when(bookingService.patch(userId, booking.getId(), true)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(patch(URL + "/{bookingId}", booking.getId())
                        .header(HEADER, userId)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
        verify(bookingService).patch(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void patch_whenStatusIsApprovedOrOwnerIsWrong_thenBadRequestReturned() {
        when(bookingService.patch(userId, booking.getId(), true)).thenThrow(ValidationException.class);
        mockMvc.perform(patch(URL + "/{bookingId}", booking.getId())
                        .header(HEADER, userId)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
        verify(bookingService).patch(anyLong(), anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getById_whenBookingFound_thenBookingReturned() {
        when(bookingService.getById(userId, booking.getId())).thenReturn(booking);
        mockMvc.perform(get(URL + "/{bookingId}", booking.getId())
                .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())));
        verify(bookingService).getById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getById_whenBookingOrUserNotFound_thenNotFoundReturned() {
        when(bookingService.getById(userId, booking.getId())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(URL + "/{bookingId}", booking.getId())
                        .header(HEADER, userId))
                .andExpect(status().isNotFound());
        verify(bookingService).getById(userId, booking.getId());
    }

    @Test
    @SneakyThrows
    void getAllByBookerId_whenBookingFoundWithoutPagination_thenBookingReturned() {
        when(bookingService.getAllByBookerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenReturn(List.of(booking));
        mockMvc.perform(get(URL)
                        .header(HEADER, userId)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())));
        verify(bookingService).getAllByBookerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void getAllByBookerId_whenBookingFoundWithPagination_thenBookingReturned() {
        when(bookingService.getAllByBookerId(userId, "WAITING", 1, 1))
                .thenReturn(List.of(booking));
        mockMvc.perform(get(URL)
                        .header(HEADER, userId)
                        .param("state", "WAITING")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())));
        verify(bookingService).getAllByBookerId(userId, "WAITING", 1, 1);
    }

    @Test
    @SneakyThrows
    void getAllByBookerId_whenBookingNotFoundWithoutPagination_thenEmptyListReturned() {
        when(bookingService.getAllByBookerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get(URL)
                        .header(HEADER, userId)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(bookingService).getAllByBookerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void getAllByBookerId_whenStateNotFound_thenBadRequestReturned() {
        when(bookingService.getAllByBookerId(userId, "other", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenThrow(ValidationException.class);
        mockMvc.perform(get(URL)
                        .header(HEADER, userId)
                        .param("state", "other"))
                .andExpect(status().isBadRequest());
        verify(bookingService).getAllByBookerId(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllByBookerId_whenUserNotFound_thenNotFoundReturned() {
        when(bookingService.getAllByBookerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(URL)
                        .header(HEADER, userId)
                        .param("state", "WAITING"))
                .andExpect(status().isNotFound());
        verify(bookingService).getAllByBookerId(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllByOwnerId_whenBookingFoundWithoutPagination_thenBookingReturned() {
        when(bookingService.getAllByOwnerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenReturn(List.of(booking));
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, userId)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())));
        verify(bookingService).getAllByOwnerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void getAllByOwnerId_whenBookingFoundWithPagination_thenBookingReturned() {
        when(bookingService.getAllByOwnerId(userId, "WAITING", 1, 1))
                .thenReturn(List.of(booking));
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, userId)
                        .param("state", "WAITING")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(booking.getItem().getName())));
        verify(bookingService).getAllByOwnerId(userId, "WAITING", 1, 1);
    }

    @Test
    @SneakyThrows
    void getAllByOwnerId_whenBookingNotFoundWithoutPagination_thenEmptyListReturned() {
        when(bookingService.getAllByOwnerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, userId)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(bookingService).getAllByOwnerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Test
    @SneakyThrows
    void getAllByOwnerId_whenStateNotFound_thenBadRequestReturned() {
        when(bookingService.getAllByOwnerId(userId, "other", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenThrow(ValidationException.class);
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, userId)
                        .param("state", "other"))
                .andExpect(status().isBadRequest());
        verify(bookingService).getAllByOwnerId(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllByOwnerId_whenUserNotFound_thenNotFoundReturned() {
        when(bookingService.getAllByOwnerId(userId, "WAITING", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE))
                .thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, userId)
                        .param("state", "WAITING"))
                .andExpect(status().isNotFound());
        verify(bookingService).getAllByOwnerId(anyLong(), anyString(), anyInt(), anyInt());
    }
}