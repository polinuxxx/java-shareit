package ru.practicum.shareit.booking.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;

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
 * Тесты для {@link BookingController}
 */
@WebMvcTest(controllers = {BookingController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private static final String URL = "/bookings";

    private static final String HEADER = "X-Sharer-User-Id";

    @MockBean
    private BookingClient bookingClient;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    private BookingCreateRequest createRequest;

    private Long userId;

    @Test
    @SneakyThrows
    void create_whenBookingStartOrEndInThePast_thenBadRequestReturned() {
        createRequest = BookingCreateRequest.builder()
                .start(LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS))
                .itemId(1L)
                .build();
        userId = 1L;
        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenBookingStartOrEndOrItemIdIsNull_thenBadRequestReturned() {
        createRequest = BookingCreateRequest.builder()
                .build();
        userId = 1L;
        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void create_whenBookingStartGreaterThanEnd_thenBadRequestReturned() {
        createRequest = BookingCreateRequest.builder()
                .start(LocalDateTime.now().plusMonths(1).truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS))
                .itemId(1L)
                .build();
        userId = 1L;
        mockMvc.perform(post(URL)
                        .header(HEADER, userId)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getAllByBookerId_whenPaginationParamsIncorrect_thenBadRequestReturned() {
        userId = 1L;
        mockMvc.perform(get(URL)
                        .header(HEADER, userId)
                        .param("state", "WAITING")
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).getAllByBookerId(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllByOwnerId_whenPaginationParamsIncorrect_thenBadRequestReturned() {
        userId = 1L;
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, userId)
                        .param("state", "WAITING")
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verify(bookingClient, never()).getAllByOwnerId(anyLong(), anyString(), anyInt(), anyInt());
    }
}