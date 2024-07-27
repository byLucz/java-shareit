package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDateDesc(Integer bookerId);
    List<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Integer ownerId);

    Optional<Booking> findFirst1ByItemIdAndBookerIdAndEndDateBefore(Integer itemId, Integer bookerId, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.startDate < CURRENT_TIMESTAMP ORDER BY b.startDate DESC")
    Optional<Booking> findLastBookingByItemId(Integer itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.startDate > CURRENT_TIMESTAMP ORDER BY b.startDate ASC")
    Optional<Booking> findNextBookingByItemId(Integer itemId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.endDate < :now ORDER BY b.startDate DESC")
    List<Booking> findAllPastUserBookings(Integer userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.startDate > :now ORDER BY b.startDate DESC")
    List<Booking> findAllFutureUserBookings(Integer userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.startDate <= :now AND b.endDate >= :now ORDER BY b.startDate DESC")
    List<Booking> findAllCurrentUserBookings(Integer userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.endDate < :now ORDER BY b.startDate DESC")
    List<Booking> findPastUserItemBookings(Integer userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.startDate > :now ORDER BY b.startDate DESC")
    List<Booking> findFutureUserItemBookings(Integer userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.startDate <= :now AND b.endDate >= :now ORDER BY b.startDate DESC")
    List<Booking> findCurrentUserBookings(Integer userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.startDate DESC")
    List<Booking> findSpecialStateUserBookings(Integer userId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = :status ORDER BY b.startDate DESC")
    List<Booking> findSpecialStateUserItemBookings(Integer userId, BookingStatus status);
}