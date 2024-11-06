package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;


@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

	private final BookingClient bookingService;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
												@RequestBody @Valid BookingRequestDto bookingRequestDto) {
		log.info("POST /bookings with userId: {} and bookingRequestDto: {}", userId, bookingRequestDto);
		return bookingService.createBooking(userId, bookingRequestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
									 @PathVariable Integer bookingId,
									 @RequestParam String approved) {
		log.info("PATCH /bookings with userId: {}, bookingId: {}, approved: {}", userId, bookingId, approved);
		return bookingService.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
								 @PathVariable Integer bookingId) {
		log.info("GET /bookings with userId: {}, bookingId: {}", userId, bookingId);
		return bookingService.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingsOfCurrentUser(@RequestHeader("X-Sharer-User-Id") int userId,
														@RequestParam(defaultValue = "ALL") String state) {
		log.info("GET /bookings with userId: {}, state: {}", userId, state);
		return bookingService.getAllBookingsOfCurrentUser(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsForAllUserItems(@RequestHeader("X-Sharer-User-Id") int userId,
														  @RequestParam(defaultValue = "ALL") String state) {
		log.info("GET /bookings/owner with userId: {}, state: {}", userId, state);
		return bookingService.getAllBookingsForAllUserItems(userId, state);
	}
}