package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.LinkedList;

public interface BookingService {
    Booking createBooking(int userId, BookingRequestDto bookingInput);
    Booking approveBooking(int userId, int bookingId, String approved);
    Booking getBookingById(int userId, int bookingId);
    LinkedList<Booking> getBookingByUser(int userId, String stateIn);
    LinkedList<Booking> getBookingByUserItems(int userId, String stateIn);
    Boolean checkUserUsedItem(int userId, int itemId);
}