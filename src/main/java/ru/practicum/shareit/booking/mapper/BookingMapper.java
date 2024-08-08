package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public Booking toBooking(BookingDto bookingInput, User user, Item item, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(bookingInput.getStart());
        booking.setEnd(bookingInput.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(status);

        return booking;
    }

    public Booking toBooking(BookingRequestDto bookingInput, User user, Item item, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(bookingInput.getStart());
        booking.setEnd(bookingInput.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(status);

        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingdto = new BookingDto();
        bookingdto.setId(booking.getId());
        bookingdto.setStart(booking.getStart());
        bookingdto.setEnd(booking.getEnd());
        bookingdto.setBooker(booking.getBooker());
        bookingdto.setItem(booking.getItem());
        bookingdto.setStatus(booking.getStatus());

        return bookingdto;
    }
}
