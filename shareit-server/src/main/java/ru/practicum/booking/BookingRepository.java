package ru.practicum.booking;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // ALL для букера
    Page<Booking> findBookingByBookerIdOrderByStartDesc(Integer bookerId, Pageable pageable);

    // ALL для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 order by b.start desc")
    Page<Booking> findAllBookingByOwner(Integer ownerId, Pageable pageable);

    // FUTURE для букера
    Page<Booking> findBookingByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId,
                                                                     LocalDateTime now,
                                                                     Pageable pageable);

    // FUTURE для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start "
            + "> now() order by b.start desc")
    Page<Booking> findAllBookingsByOwnerFuture(Integer ownerId, Pageable pageable);

    // CURRENT для букера
    @Query("select b from Booking b where b.booker.id = ?1 and b.start "
            + "< now() and b.end > now() order by b.start desc")
    Page<Booking> findAllBookingByBookerCurrent(Integer bookerId, Pageable pageable);

    // CURRENT для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start "
            + "< now() and b.end > now() order by b.start desc")
    Page<Booking> findAllBookingByOwnerCurrent(Integer ownerId, Pageable pageable);

    // PAST для букера
    @Query("select b from Booking b where b.booker.id = ?1 and b.end < now() order by b.start desc")
    Page<Booking> findAllBookingByBookerPast(Integer bookerId, Pageable pageable);

    // PAST для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 and b.end "
            + "< now() order by b.start desc")
    Page<Booking> findAllBookingByOwnerPast(Integer ownerId, Pageable pageable);

    // WAITING, APPROVED, REJECTED, CANCELLED для букера
    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.start desc")
    Page<Booking> findBookingOfBookerByStatus(Integer bookerId, BookingState status,
                                              Pageable pageable);

    // WAITING, APPROVED, REJECTED, CANCELLED для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 "
            + "and b.status = ?2 order by b.start desc")
    Page<Booking> findBookingOfOwnerByStatus(Integer ownerId, BookingState status,
                                             Pageable pageable);

    // последнее бронирование
    Booking findTopBookingByItemIdAndStartBeforeOrderByStartDesc(Integer itemId, LocalDateTime now);

    // следующее бронирование
    Booking findTopBookingByItemIdAndStartAfterOrderByStart(Integer itemId, LocalDateTime now);

    // даты окончаний бронирований букера
    @Query("select b.end from Booking b where b.booker.id = ?1 and b.item.id = ?2")
    List<LocalDateTime> getListBookingEndDate(Integer bookerId, Integer itemId);

    // Проверка на непересечение броней
    @Query(nativeQuery = true, value = "select exists (select * from bookings b "
            + "where b.item_id = ?1 and b.start_date <= ?3 and b.end_date >= ?2 "
            + "and b.status = 'APPROVED')")
    boolean findOverlapsBookings(Integer itemId, LocalDateTime start, LocalDateTime end);
}
