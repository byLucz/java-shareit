package ru.practicum.shareit.exceptions;

public class UserServiceException extends IllegalArgumentException {
    public UserServiceException(String message) {
        super(message);
    }
}