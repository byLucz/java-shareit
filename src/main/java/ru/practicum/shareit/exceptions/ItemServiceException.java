package ru.practicum.shareit.exceptions;

public class ItemServiceException extends RuntimeException {
    public ItemServiceException(String message) {
        super(message);
    }
}