package ru.practicum.shareit.exceptions;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.user.controller.UserController;

import java.util.Map;

@RestControllerAdvice(basePackageClasses = {ItemController.class, UserController.class, BookingController.class})

@Slf4j

public class ExHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final RuntimeException e) {
        log.debug("Получен статус 400 Validation Exception {}", e.getMessage(), e);
        return Map.of("ERROR", "Ошибка валидации данных", "ErrorMessage", e.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherException(final RuntimeException e) {
        log.debug("Получен статус 500 Internal Server Exception {}", e.getMessage(), e);
        return Map.of(
                "ERROR", "Возникло исключение", "ErrorMessage", e.getMessage()
        );
    }
}