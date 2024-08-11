package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        Item item = new Item(1, "Test Item", "Description", true, null, null);
        User user = new User(1, "Test User", "user@test.com");
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.of(2024, 8, 10, 12, 0),
                LocalDateTime.of(2024, 8, 10, 14, 0), item, user, BookingStatus.APPROVED);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id")
                .hasJsonPath("$.start")
                .hasJsonPath("$.end")
                .hasJsonPath("$.status")
                .hasJsonPath("$.booker.id")
                .hasJsonPath("$.booker.name")
                .hasJsonPath("$.booker.email")
                .hasJsonPath("$.item.id")
                .hasJsonPath("$.item.name")
                .hasJsonPath("$.item.available")
                .hasJsonPath("$.item.description");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingDto.getBooker().getId());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookingDto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(bookingDto.getBooker().getEmail());

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingDto.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(bookingDto.getItem().getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(bookingDto.getItem().getAvailable());
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"start\":\"2024-08-10T12:00:00\",\"end\":\"2024-08-10T14:00:00\"," +
                "\"item\":{\"id\":1,\"name\":\"Test Item\",\"description\":\"Description\",\"available\":true}," +
                "\"booker\":{\"id\":1,\"name\":\"Test User\",\"email\":\"user@test.com\"}," +
                "\"status\":\"APPROVED\"}";

        BookingDto bookingDto = this.json.parse(content).getObject();

        assertThat(bookingDto.getId()).isEqualTo(1);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2024, 8, 10, 12, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 8, 10, 14, 0));
        assertThat(bookingDto.getItem().getId()).isEqualTo(1);
        assertThat(bookingDto.getItem().getName()).isEqualTo("Test Item");
        assertThat(bookingDto.getItem().getDescription()).isEqualTo("Description");
        assertThat(bookingDto.getItem().getAvailable()).isTrue();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("Test User");
        assertThat(bookingDto.getBooker().getEmail()).isEqualTo("user@test.com");
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}
