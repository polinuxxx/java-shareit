package ru.practicum.shareit.booking.dao;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

/**
 * ДАО для {@link Booking}.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long userId,
                                                                            LocalDateTime start, LocalDateTime end,
                                                                                            Pageable pageable);

    List<Booking> findByBookerIdAndEndLessThanOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long userId,
                                                                            LocalDateTime start, LocalDateTime end,
                                                                                               Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndLessThanOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartGreaterThanOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    Booking findFirstByItemIdAndStartLessThanEqualAndStatusIsOrderByStartDesc(Long itemId, LocalDateTime end,
                                                                          BookingStatus status);

    Booking findFirstByItemIdAndStartGreaterThanAndStatusIsOrderByStartAsc(Long itemId, LocalDateTime start,
                                                                           BookingStatus status);

    List<Booking> findFirstByItemIdInAndStartLessThanEqualAndStatusIsOrderByStartDesc(List<Long> itemIds,
                                                                          LocalDateTime end, BookingStatus status);

    List<Booking> findFirstByItemIdInAndStartGreaterThanAndStatusIsOrderByStartAsc(List<Long> itemIds,
                                                                           LocalDateTime start, BookingStatus status);

    Booking findFirstByBookerIdAndItemIdAndEndLessThan(Long userId, Long itemId, LocalDateTime end);

    default Map<Long, Booking> findAllLastBookings(List<Long> itemIds, LocalDateTime end, BookingStatus status) {
        return findFirstByItemIdInAndStartLessThanEqualAndStatusIsOrderByStartDesc(itemIds, end, status).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
    }

    default Map<Long, Booking> findAllNextBookings(List<Long> itemIds, LocalDateTime start, BookingStatus status) {
        return findFirstByItemIdInAndStartGreaterThanAndStatusIsOrderByStartAsc(itemIds, start, status).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));
    }
}
