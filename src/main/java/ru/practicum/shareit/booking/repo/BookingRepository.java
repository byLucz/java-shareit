package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    Optional<Booking> findLastBookingByItemId(Integer itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start ASC")
    Optional<Booking> findNextBookingByItemId(Integer itemId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    LinkedList<Booking> getAllUserBookings(@Param("userId") Integer userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :now ORDER BY b.start DESC")
    LinkedList<Booking> getAllFutureUserBookings(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.start DESC")
    LinkedList<Booking> getSpecialStateUserBookings(@Param("userId") Integer userId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < :now ORDER BY b.start DESC")
    LinkedList<Booking> getAllPastUserBookings(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    LinkedList<Booking> getAllCurrentUserBookings(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId ORDER BY b.start DESC")
    LinkedList<Booking> getAllUserItemBookings(@Param("userId") Integer userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start > :now ORDER BY b.start DESC")
    LinkedList<Booking> getFutureUserItemBookings(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = :status ORDER BY b.start DESC")
    LinkedList<Booking> getSpecialStateUserItemBookings(@Param("userId") Integer userId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.end < :now ORDER BY b.start DESC")
    LinkedList<Booking> getPastUserItemBookings(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    LinkedList<Booking> getCurrentUserBookings(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :userId AND b.end < :now ORDER BY b.end DESC")
    Optional<Booking> findFirst1ByItemIdAndBookerIdAndEndIsBefore(@Param("itemId") Integer itemId, @Param("userId") Integer userId, @Param("now") LocalDateTime now);
}