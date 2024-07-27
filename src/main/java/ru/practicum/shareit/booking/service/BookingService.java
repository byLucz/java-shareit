package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Integer userId, BookingRequestDto bookingInput);
    BookingDto approveBooking(Integer userId, Integer bookingId, boolean approved);
    BookingDto getBookingById(Integer userId, Integer bookingId);

    List<BookingDto> getBookingsByUser(Integer userId, String state);
    List<BookingDto> getBookingsByOwner(Integer userId, String state);
    boolean checkUserUsedItem(Integer userId, Integer itemId);
}