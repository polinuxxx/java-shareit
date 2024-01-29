package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для {@link BookingServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private static final Integer DEFAULT_PAGE_START = 0;

    private static final Integer DEFAULT_PAGE_SIZE = 10;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Long userId;

    private Long itemId;

    private Item item;

    private User owner;

    private User booker;

    private Booking currentBooking;

    private Booking pastBooking;

    private Booking futureBooking;

    private Booking waitingBooking;

    private Booking rejectedBooking;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 1L;
        item = Item.builder()
                .id(itemId)
                .name("thing")
                .description("description")
                .available(true)
                .build();
        owner = User.builder()
                .id(1L)
                .name("Jack Sparrow")
                .email("sparrow@gmail.com")
                .build();
        booker = User.builder()
                .id(2L)
                .name("William Turner")
                .email("bootstrap@gmail.com")
                .build();
        currentBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        pastBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        futureBooking = Booking.builder()
                .id(3L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        waitingBooking = Booking.builder()
                .id(4L)
                .start(LocalDateTime.now().plusDays(4))
                .end(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        rejectedBooking = Booking.builder()
                .id(5L)
                .start(LocalDateTime.now().plusDays(4))
                .end(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();
        pageable = PageRequest.of(DEFAULT_PAGE_START / DEFAULT_PAGE_SIZE, DEFAULT_PAGE_SIZE);
    }

    @Test
    void create_whenBookingIsValid_thenBookingSaved() {
        when(userRepository.existsById(userId)).thenReturn(true);
        item.setOwner(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(waitingBooking)).thenReturn(waitingBooking);

        Booking actual = bookingService.create(userId, waitingBooking);

        assertThat(actual.getId(), equalTo(waitingBooking.getId()));
        assertThat(actual.getStart(), equalTo(waitingBooking.getStart()));
        assertThat(actual.getEnd(), equalTo(waitingBooking.getEnd()));
        assertThat(actual.getBooker().getId(), equalTo(waitingBooking.getBooker().getId()));
        assertThat(actual.getItem().getId(), equalTo(waitingBooking.getItem().getId()));
        assertThat(actual.getItem().getName(), equalTo(waitingBooking.getItem().getName()));
        assertThat(actual.getStatus(), equalTo(waitingBooking.getStatus()));

        verify(bookingRepository).save(waitingBooking);
    }

    @Test
    void create_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId, waitingBooking));

        verify(bookingRepository, never()).save(waitingBooking);
    }

    @Test
    void create_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId, waitingBooking));

        verify(bookingRepository, never()).save(waitingBooking);
    }

    @Test
    void create_whenItemNotAvailable_thenValidationExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(true);
        item.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(userId, waitingBooking));

        verify(bookingRepository, never()).save(waitingBooking);
    }

    @Test
    void create_whenOwnerEqualsBooker_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(true);
        item.setOwner(owner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(userId, waitingBooking));

        verify(bookingRepository, never()).save(waitingBooking);
    }

    @Test
    void patch_whenBookingIsValid_thenBookingUpdated() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(waitingBooking.getId())).thenReturn(Optional.of(waitingBooking));
        item.setOwner(owner);
        waitingBooking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.save(waitingBooking)).thenReturn(waitingBooking);

        Booking actual = bookingService.patch(userId, waitingBooking.getId(), false);

        assertThat(actual.getId(), equalTo(waitingBooking.getId()));
        assertThat(actual.getStart(), equalTo(waitingBooking.getStart()));
        assertThat(actual.getEnd(), equalTo(waitingBooking.getEnd()));
        assertThat(actual.getBooker().getId(), equalTo(waitingBooking.getBooker().getId()));
        assertThat(actual.getItem().getId(), equalTo(waitingBooking.getItem().getId()));
        assertThat(actual.getItem().getName(), equalTo(waitingBooking.getItem().getName()));
        assertThat(actual.getStatus(), equalTo(waitingBooking.getStatus()));

        verify(bookingRepository).save(waitingBooking);
    }

    @Test
    void patch_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.patch(userId, waitingBooking.getId(), true));

        verify(bookingRepository, never()).save(waitingBooking);
    }

    @Test
    void patch_whenBookingStatusApproved_thenValidationExceptionThrown() {
        when(userRepository.existsById(userId)).thenReturn(true);
        waitingBooking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(waitingBooking.getId())).thenReturn(Optional.of(waitingBooking));
        item.setOwner(owner);

        assertThrows(ValidationException.class,
                () -> bookingService.patch(userId, waitingBooking.getId(), true));

        verify(bookingRepository, never()).save(waitingBooking);
    }

    @Test
    void getById_whenBookingFound_thenBookingReturned() {
        when(bookingRepository.findById(currentBooking.getId())).thenReturn(Optional.of(currentBooking));

        item.setOwner(owner);
        Booking actual = bookingService.getById(userId, currentBooking.getId());

        assertThat(actual.getId(), equalTo(currentBooking.getId()));
        assertThat(actual.getStart(), equalTo(currentBooking.getStart()));
        assertThat(actual.getEnd(), equalTo(currentBooking.getEnd()));
        assertThat(actual.getBooker().getId(), equalTo(currentBooking.getBooker().getId()));
        assertThat(actual.getItem().getId(), equalTo(currentBooking.getItem().getId()));
        assertThat(actual.getItem().getName(), equalTo(currentBooking.getItem().getName()));
        assertThat(actual.getStatus(), equalTo(currentBooking.getStatus()));

        verify(bookingRepository).findById(currentBooking.getId());

    }

    @Test
    void getById_whenBookingNotFound_thenEntityNotFoundExceptionThrown() {
        when(bookingRepository.findById(currentBooking.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getById(userId, currentBooking.getId()));
    }

    @Test
    void getById_whenNotBookerAndNotOwner_thenEntityNotFoundExceptionThrown() {
        when(bookingRepository.findById(currentBooking.getId())).thenReturn(Optional.of(currentBooking));
        item.setOwner(owner);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getById(3L, currentBooking.getId()));
    }

    @Test
    void getAllByBookerId_whenAllBookingsSearched_thenAllBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), pageable))
                .thenReturn(List.of(currentBooking, pastBooking, futureBooking, waitingBooking, rejectedBooking));

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(5));
    }

    @Test
    void getAllByBookerId_whenCurrentBookingsSearched_thenCurrentBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(anyLong(),
                any(), any(), any()))
                .thenReturn(List.of(currentBooking));

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "CURRENT",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(currentBooking));
    }

    @Test
    void getAllByBookerId_whenPastBookingsSearched_thenPastBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(pastBooking));

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "PAST",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(pastBooking));
    }

    @Test
    void getAllByBookerId_whenFutureBookingsSearched_thenFutureBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(futureBooking));

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "FUTURE",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(futureBooking));
    }

    @Test
    void getAllByBookerId_whenWaitingBookingsSearched_thenWaitingBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(booker.getId(), BookingStatus.WAITING, pageable))
                .thenReturn(List.of(waitingBooking));

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "WAITING",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(waitingBooking));
    }

    @Test
    void getAllByBookerId_whenRejectedBookingsSearched_thenRejectedBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(booker.getId(), BookingStatus.REJECTED, pageable))
                .thenReturn(List.of(rejectedBooking));

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "REJECTED",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(rejectedBooking));
    }

    @Test
    void getAllByBookerId_whenWrongStateSearched_thenValidationExceptionThrown() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);

        assertThrows(ValidationException.class, () -> bookingService.getAllByBookerId(booker.getId(),
                "other", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE));
    }

    @Test
    void getAllByBookerId_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(booker.getId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllByBookerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE));

        verify(bookingRepository, never()).findByBookerIdOrderByStartDesc(booker.getId(), pageable);
    }

    @Test
    void getAllByBookerId_whenAllBookingsWithPaginationSearched_thenNotAllBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(currentBooking));

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, 1);

        assertThat(all, hasSize(1));

        verify(bookingRepository).findByBookerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 1));
    }

    @Test
    void getAllByBookerId_whenBookingsNotFoundSearched_thenEmptyListReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(booker.getId(), pageable)).thenReturn(Collections.emptyList());

        List<Booking> all = bookingService.getAllByBookerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(0));

        verify(bookingRepository).findByBookerIdOrderByStartDesc(booker.getId(), pageable);
    }

    @Test
    void getAllByOwnerId_whenAllBookingsSearched_thenAllBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(booker.getId(), pageable))
                .thenReturn(List.of(currentBooking, pastBooking, futureBooking, waitingBooking, rejectedBooking));

        List<Booking> all = bookingService.getAllByOwnerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(5));
    }

    @Test
    void getAllByOwnerId_whenCurrentBookingsSearched_thenCurrentBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(anyLong(),
                any(), any(), any()))
                .thenReturn(List.of(currentBooking));

        List<Booking> all = bookingService.getAllByOwnerId(booker.getId(), "CURRENT",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(currentBooking));
    }

    @Test
    void getAllByOwnerId_whenPastBookingsSearched_thenPastBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndEndLessThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(pastBooking));

        List<Booking> all = bookingService.getAllByOwnerId(booker.getId(), "PAST",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(pastBooking));
    }

    @Test
    void getAllByOwnerId_whenFutureBookingsSearched_thenFutureBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(futureBooking));

        List<Booking> all = bookingService.getAllByOwnerId(booker.getId(), "FUTURE",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(futureBooking));
    }

    @Test
    void getAllByOwnerId_whenWaitingBookingsSearched_thenWaitingBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(booker.getId(), BookingStatus.WAITING, pageable))
                .thenReturn(List.of(waitingBooking));

        List<Booking> all = bookingService.getAllByOwnerId(booker.getId(), "WAITING",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(waitingBooking));
    }

    @Test
    void getAllByOwnerId_whenRejectedBookingsSearched_thenRejectedBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDesc(booker.getId(), BookingStatus.REJECTED, pageable))
                .thenReturn(List.of(rejectedBooking));

        List<Booking> all = bookingService.getAllByOwnerId(booker.getId(), "REJECTED",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(rejectedBooking));
    }

    @Test
    void getAllByOwnerId_whenWrongStateSearched_thenValidationExceptionThrown() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);

        assertThrows(ValidationException.class, () -> bookingService.getAllByOwnerId(booker.getId(),
                "other", DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE));
    }

    @Test
    void getAllByOwnerId_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
        when(userRepository.existsById(booker.getId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllByOwnerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE));

        verify(bookingRepository, never()).findByItemOwnerIdOrderByStartDesc(booker.getId(), pageable);
    }

    @Test
    void getAllByOwnerId_whenAllBookingsWithPaginationSearched_thenNotAllBookingsReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(currentBooking));

        List<Booking> all = bookingService.getAllByOwnerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, 1);

        assertThat(all, hasSize(1));

        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(booker.getId(), PageRequest.of(0, 1));
    }

    @Test
    void getAllByOwnerId_whenBookingsNotFoundSearched_thenEmptyListReturned() {
        when(userRepository.existsById(booker.getId())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(booker.getId(), pageable)).thenReturn(Collections.emptyList());

        List<Booking> all = bookingService.getAllByOwnerId(booker.getId(), "ALL",
                DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);

        assertThat(all, hasSize(0));

        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(booker.getId(), pageable);
    }
}