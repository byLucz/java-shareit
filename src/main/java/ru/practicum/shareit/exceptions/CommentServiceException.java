package ru.practicum.shareit.exceptions;

public class CommentServiceException extends RuntimeException {
    public CommentServiceException(String message) {
        super(message);
    }
}
