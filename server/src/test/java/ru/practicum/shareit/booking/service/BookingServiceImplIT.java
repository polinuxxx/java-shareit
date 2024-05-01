package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Интеграционные тесты для {@link BookingServiceImpl}
 */
@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIT {
    private static final Integer DEFAULT_PAGE_START = 0;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private final BookingService bookingService;

    private final ItemService itemService;

    private final UserService userService;

    private Booking currentBooking;

    private Booking pastBooking;

    private Booking futureBooking;

    private Booking waitingBooking;

    private Booking rejectedBooking;

    private Item item;

    private User owner;

    private User booker;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        owner = User.builder()
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();
        booker = User.builder()
                .name("William Turner")
                .email("bootstrap@gmail.com")
                .build();
        item = Item.builder()
                .name("thing")
                .description("description")
                .available(true)
                .build();
        currentBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        pastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        futureBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        waitingBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(4))
                .end(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        rejectedBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(4))
                .end(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();
    }

    @Test
    void getAllByBookerId_whenAllBookingsSearched_thenAllBookingsReturned() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);
        bookingService.create(booker.getId(), currentBooking);
        bookingService.create(booker.getId(), pastBooking);
        bookingService.create(booker.getId(), futureBooking);
        bookingService.create(booker.getId(), waitingBooking);
        bookingService.create(booker.getId(), rejectedBooking);

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(5));
    }

    @Test
    void getAllByBookerId_whenCurrentBookingsSearched_thenCurrentBookingsReturned() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);
        bookingService.create(booker.getId(), currentBooking);
        bookingService.create(booker.getId(), pastBooking);
        bookingService.create(booker.getId(), futureBooking);
        bookingService.create(booker.getId(), waitingBooking);
        bookingService.create(booker.getId(), rejectedBooking);

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "CURRENT",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(currentBooking));
    }

    @Test
    void getAllByBookerId_whenPastBookingsSearched_thenPastBookingsReturned() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);
        bookingService.create(booker.getId(), currentBooking);
        bookingService.create(booker.getId(), pastBooking);
        bookingService.create(booker.getId(), futureBooking);
        bookingService.create(booker.getId(), waitingBooking);
        bookingService.create(booker.getId(), rejectedBooking);

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "PAST",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(pastBooking));
    }

    @Test
    void getAllByBookerId_whenFutureBookingsSearched_thenFutureBookingsReturned() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);
        bookingService.create(booker.getId(), currentBooking);
        bookingService.create(booker.getId(), pastBooking);
        bookingService.create(booker.getId(), futureBooking);

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "FUTURE",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(futureBooking));
    }

    @Test
    void getAllByBookerId_whenWaitingBookingsSearched_thenWaitingBookingsReturned() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);
        bookingService.create(booker.getId(), currentBooking);
        bookingService.create(booker.getId(), pastBooking);
        bookingService.create(booker.getId(), futureBooking);
        bookingService.create(booker.getId(), waitingBooking);
        bookingService.create(booker.getId(), rejectedBooking);

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "WAITING",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(waitingBooking));
    }

    @Test
    void getAllByBookerId_whenRejectedBookingsSearched_thenRejectedBookingsReturned() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);
        bookingService.create(booker.getId(), currentBooking);
        bookingService.create(booker.getId(), pastBooking);
        bookingService.create(booker.getId(), futureBooking);
        bookingService.create(booker.getId(), waitingBooking);
        bookingService.create(booker.getId(), rejectedBooking);

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "REJECTED",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(rejectedBooking));
    }

    @Test
    void getAllByBookerId_whenWrongStateSearched_thenValidationExceptionThrown() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);
        bookingService.create(booker.getId(), currentBooking);

        assertThrows(ValidationException.class, () -> bookingService.getAllByBookerId(booker.getId(),
                "other", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE));
    }

    @Test
    void getAllByBookerId_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllByBookerId(userId,
                "other", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE));
    }

    @Test
    void getAllByBookerId_whenAllBookingsWithPaginationSearched_thenNotAllBookingsReturned() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);
        bookingService.create(booker.getId(), currentBooking);
        bookingService.create(booker.getId(), pastBooking);
        bookingService.create(booker.getId(), futureBooking);
        bookingService.create(booker.getId(), waitingBooking);
        bookingService.create(booker.getId(), rejectedBooking);

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, 1);

        assertThat(all, hasSize(1));
    }

    @Test
    void getAllByBookerId_whenBookingsNotFoundSearched_thenEmptyListReturned() {
        userService.create(booker);
        userService.create(owner);
        itemService.create(owner.getId(), item);

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(0));
    }
}