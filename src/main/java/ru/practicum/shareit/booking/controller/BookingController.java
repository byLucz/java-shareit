package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.info("POST /bookings with userId: {} and bookingRequestDto: {}", userId, bookingRequestDto);
        return bookingService.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                  @PathVariable Integer bookingId,
                                  @RequestParam String approved) {
        log.info("PATCH /bookings with userId: {}, bookingId: {}, approved: {}", userId, bookingId, approved);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer bookingId) {
        log.info("GET /bookings with userId: {}, bookingId: {}", userId, bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getAllBookingsOfCurrentUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings with userId: {}, state: {}", userId, state);
        return bookingService.getBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingsForAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner with userId: {}, state: {}", userId, state);
        return bookingService.getBookingByUserItems(userId, state);
    }
}