package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> getBookingById(Integer bookingId);

    // ALL для букера
    List<Booking> findBookingByBookerIdOrderByStartDesc(Integer bookerId);

    // ALL для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 order by b.start desc")
    List<Booking> findAllBookingByOwner(Integer ownerId);

    // FUTURE для букера
    List<Booking> findBookingByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId,
                                                                     LocalDateTime now);

    // FUTURE для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start "
            + "> now() order by b.start desc")
    List<Booking> findAllBookingByOwnerFuture(Integer ownerId);

    // CURRENT для букера
    @Query("select b from Booking b where b.booker.id = ?1 and b.start "
            + "< now() and b.end > now() order by b.start desc")
    List<Booking> findAllBookingByBookerIdCurrent(Integer bookerId);

    // CURRENT для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 and b.start "
            + "< now() and b.end > now() order by b.start desc")
    List<Booking> findAllBookingByOwnerIdCurrent(Integer ownerId);

    // PAST для букера
    @Query("select b from Booking b where b.booker.id = ?1 and b.end < now() order by b.start desc")
    List<Booking> findAllBookingByBookerPast(Integer bookerId);

    // PAST для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 and b.end "
            + "< now() order by b.start desc")
    List<Booking> findAllBookingByOwnerPast(Integer ownerId);

    // WAITING, APPROVED, REJECTED, CANCELLED для букера
    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findBookingOfBookerByStatus(Integer bookerId, BookingState status);

    // WAITING, APPROVED, REJECTED, CANCELLED для хозяина вещей
    @Query("select b from Booking b where b.item.ownerId = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findBookingOfOwnerByStatus(Integer ownerId, BookingState status);

    // последнее бронирование
    Booking findTopBookingByItemIdAndStartBeforeOrderByStartDesc(Integer itemId, LocalDateTime now);

    // следующее бронирование
    Booking findTopBookingByItemIdAndStartAfterOrderByStart(Integer itemId, LocalDateTime now);

    // даты окончаний бронирований букера
    @Query("select b.end from Booking b where b.booker.id = ?1 and b.item.id = ?2")
    List<LocalDateTime> getListBookingEndDate(Integer bookerId, Integer itemId);

    // Проверка на непересечение броней
    @Query(nativeQuery = true, value = "select exists (select * from bookings b "
            + "where b.item_id = ?1 and b.start_date <= ?3 and b.end_date >= ?2)")
    boolean findOverlapsBookings(Integer itemId, LocalDateTime start, LocalDateTime end);
}
