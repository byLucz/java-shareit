package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(null, "Test User", "user@test.com");
        owner = new User(null, "Owner", "owner@test.com");
        userRepository.save(user);
        userRepository.save(owner);

        item = new Item(null, "Test Item", "Test Description", true, owner, null);
        itemRepository.save(item);
    }

    @Test
    void testCreateBooking() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item.getId());

        BookingDto createdBooking = bookingService.createBooking(user.getId(), bookingRequestDto);

        assertNotNull(createdBooking);
        assertEquals(bookingRequestDto.getStart(), createdBooking.getStart());
        assertEquals(bookingRequestDto.getEnd(), createdBooking.getEnd());
        assertEquals(item.getId(), createdBooking.getItem().getId());
        assertEquals(user.getId(), createdBooking.getBooker().getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());

        assertTrue(bookingRepository.findById(createdBooking.getId()).isPresent());
    }

    @Test
    void testApproveBooking() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),item.getId());
        BookingDto createdBooking = bookingService.createBooking(user.getId(), bookingRequestDto);

        BookingDto approvedBooking = bookingService.approveBooking(owner.getId(), createdBooking.getId(), "true");

        assertNotNull(approvedBooking);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());

        assertEquals(BookingStatus.APPROVED, bookingRepository.findById(createdBooking.getId()).get().getStatus());
    }

    @Test
    void testGetBookingById() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),item.getId());
        BookingDto createdBooking = bookingService.createBooking(user.getId(), bookingRequestDto);

        BookingDto foundBooking = bookingService.getBookingById(user.getId(), createdBooking.getId());

        assertNotNull(foundBooking);
        assertEquals(createdBooking.getId(), foundBooking.getId());
    }

    @Test
    void testGetBookingByUser() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),item.getId());
        bookingService.createBooking(user.getId(), bookingRequestDto);

        List<BookingDto> bookings = bookingService.getBookingByUser(user.getId(), "ALL");

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByUserItems() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),item.getId());
        bookingService.createBooking(user.getId(), bookingRequestDto);

        LinkedList<BookingDto> bookings = bookingService.getBookingByUserItems(owner.getId(), "ALL");

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.WAITING, bookings.getFirst().getStatus());
        assertEquals(item.getId(), bookings.getFirst().getItem().getId());
    }

}