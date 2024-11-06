package ru.practicum.shareit.exceptions;

public class BookingServiceException extends RuntimeException {
    public BookingServiceException(String message) {
        super(message);
    }
}
