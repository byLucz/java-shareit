package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exceptions.BookingServiceException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper mapper;

    @Transactional
    public Booking createBooking(int userId, BookingRequestDto bookingInput) {
        Item item = itemService.getItemById(bookingInput.getItemId());
        validateBookingDates(bookingInput.getStart(), bookingInput.getEnd());
        if (!item.getAvailable())
            throw new BookingServiceException("Вещь недоступна для бронирования");
        if (item.getOwner().getId().equals(userId))
            throw new NotFoundException("Владелец не может забронировать свою вещь");

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking booking = mapper.toBooking(bookingInput, booker, item, BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking approveBooking(int userId, int bookingId, String approved) {
        if (!(approved.equalsIgnoreCase("true")) && !(approved.equalsIgnoreCase("false")))
            throw new BookingServiceException("Неправильный параметр approved");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new BookingServiceException("Заявка уже рассмотрена");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BookingServiceException("У пользователя нет бронирований"));

        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            if (!booking.getBooker().getId().equals(user.getId()))
                throw new BookingServiceException("У вас нет доступа к бронированию");
            else
                throw new NotFoundException("Вы не владелец вещи");
        }

        booking.setStatus(approved.equalsIgnoreCase("true") ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(int userId, int bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId))
            throw new NotFoundException("Пользователь не является владельцем вещи или бронирования");

        return booking;
    }

    public LinkedList<Booking> getBookingByUser(int userId, String stateIn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LinkedList<Booking> bookingLinkedList = new LinkedList<>();
        if (BookingState.findByName(stateIn) == null)
            throw new RuntimeException("Unknown state: " + stateIn);
        BookingState state = BookingState.findByName(stateIn);
        switch (state) {
            case ALL:
                bookingLinkedList = bookingRepository.getAllUserBookings(userId);
                break;
            case FUTURE:
                bookingLinkedList = bookingRepository.getAllFutureUserBookings(userId, LocalDateTime.now());
                break;
            case WAITING:
            case REJECTED:
                bookingLinkedList = bookingRepository.getSpecialStateUserBookings(userId, BookingStatus.valueOf(state.name()));
                break;
            case PAST:
                bookingLinkedList = bookingRepository.getAllPastUserBookings(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingLinkedList = bookingRepository.getAllCurrentUserBookings(userId, LocalDateTime.now());
                break;
        }
        return bookingLinkedList;
    }

    public LinkedList<Booking> getBookingByUserItems(int userId, String stateIn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LinkedList<Booking> bookingLinkedList = new LinkedList<>();
        if (BookingState.findByName(stateIn) == null)
            throw new RuntimeException("Unknown state: " + stateIn);
        BookingState state = BookingState.findByName(stateIn);
        switch (state) {
            case ALL:
                bookingLinkedList = bookingRepository.getAllUserItemBookings(userId);
                break;
            case FUTURE:
                bookingLinkedList = bookingRepository.getFutureUserItemBookings(userId, LocalDateTime.now());
                break;
            case WAITING:
            case REJECTED:
                bookingLinkedList = bookingRepository.getSpecialStateUserItemBookings(userId, BookingStatus.valueOf(state.name()));
                break;
            case PAST:
                bookingLinkedList = bookingRepository.getPastUserItemBookings(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingLinkedList = bookingRepository.getCurrentUserBookings(userId, LocalDateTime.now());
                break;
        }
        return bookingLinkedList;
    }

    private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())
                || end.isBefore(start) || start.isEqual(end)) {
            throw new BookingServiceException("Некорректные даты бронирования");
        }
    }

    public Boolean checkUserUsedItem(int userId, int itemId) {
        return bookingRepository.findFirst1ByItemIdAndBookerIdAndEndIsBefore(itemId, userId, LocalDateTime.now()).isPresent();
    }
}