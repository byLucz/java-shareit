package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exceptions.BookingServiceException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingDto createBooking(Integer userId, BookingRequestDto bookingInput) {
        Item item = itemService.getItemById(bookingInput.getItemId());
        if (!item.getAvailable()) {
            throw new BookingServiceException("Вещь недоступна для бронирования");
        }
        validateBookingDates(bookingInput.getStartDate(), bookingInput.getEndDate());

        if (item.getOwner().getId().equals(userId)) {
            throw new BookingServiceException("Владелец не может забронировать свою вещь");
        }

        User booker = userService.getUserById(userId);
        Booking booking = bookingMapper.toBooking(bookingInput, booker, item, BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    public BookingDto approveBooking(Integer userId, Integer bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Заявка уже рассмотрена");
        }

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingServiceException("Только владелец может подтвердить бронирование");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Integer userId, Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingServiceException("Пользователь не является владельцем вещи или бронирования");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(Integer userId, String state) {
        userService.getUserById(userId);
        List<Booking> bookings = filterBookingsByState(userId, state, true);
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwner(Integer userId, String state) {
        userService.getUserById(userId);
        List<Booking> bookings = filterBookingsByState(userId, state, false);
        return bookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private List<Booking> filterBookingsByState(Integer userId, String state, boolean isUser) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new LinkedList<>();
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = isUser ? bookingRepository.findAllByBookerIdOrderByStartDateDesc(userId)
                        : bookingRepository.findAllByItemOwnerIdOrderByStartDateDesc(userId);
                break;
            case "CURRENT":
                bookings = isUser ? bookingRepository.findAllCurrentUserBookings(userId, now)
                        : bookingRepository.findCurrentUserBookings(userId, now);
                break;
            case "PAST":
                bookings = isUser ? bookingRepository.findAllPastUserBookings(userId, now)
                        : bookingRepository.findPastUserItemBookings(userId, now);
                break;
            case "FUTURE":
                bookings = isUser ? bookingRepository.findAllFutureUserBookings(userId, now)
                        : bookingRepository.findFutureUserItemBookings(userId, now);
                break;
            case "WAITING":
            case "REJECTED":
                BookingStatus status = BookingStatus.valueOf(state.toUpperCase());
                bookings = isUser ? bookingRepository.findSpecialStateUserBookings(userId, status)
                        : bookingRepository.findSpecialStateUserItemBookings(userId, status);
                break;
            default:
                throw new RuntimeException("Unknown state: " + state);
        }
        return bookings;
    }

    private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())
                || end.isBefore(start) || start.isEqual(end)) {
            throw new BookingServiceException("Некорректные даты бронирования");
        }
    }

    public boolean checkUserUsedItem(Integer userId, Integer itemId) {
        return bookingRepository.findFirst1ByItemIdAndBookerIdAndEndDateBefore(itemId, userId, LocalDateTime.now()).isPresent();
    }
}
