package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.LinkedList;

public interface BookingService {
    BookingDto createBooking(int userId, BookingRequestDto bookingInput);

    BookingDto approveBooking(int userId, int bookingId, String approved);

    BookingDto getBookingById(int userId, int bookingId);

    LinkedList<BookingDto> getBookingByUser(int userId, String stateIn);

    LinkedList<BookingDto> getBookingByUserItems(int userId, String stateIn);

    Boolean checkUserUsedItem(int userId, int itemId);
}