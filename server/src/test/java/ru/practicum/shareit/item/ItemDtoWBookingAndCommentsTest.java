package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWBookingAndComments;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoWBookingAndCommentsTest {

    @Autowired
    private JacksonTester<ItemDtoWBookingAndComments> json;

    @Test
    void testSerialize() throws Exception {
        CommentDto comment = new CommentDto(1, "Great item!", "User1", LocalDateTime.of(2024, 8, 10, 12, 0));
        BookingDto lastBooking = new BookingDto(1, LocalDateTime.of(2024, 8, 10, 12, 0), LocalDateTime.of(2024, 8, 11, 12, 0), null, null, BookingStatus.APPROVED);
        BookingDto nextBooking = new BookingDto(2, LocalDateTime.of(2024, 8, 12, 12, 0), LocalDateTime.of(2024, 8, 13, 12, 0), null, null, BookingStatus.APPROVED);

        ItemDtoWBookingAndComments itemDto = new ItemDtoWBookingAndComments(1, "ItemName", "ItemDescription", true, lastBooking, nextBooking, List.of(comment));

        JsonContent<ItemDtoWBookingAndComments> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(lastBooking.getId());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo(lastBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo(lastBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(nextBooking.getId());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo(nextBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo(nextBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(comment.getId());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo(comment.getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo(comment.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo(comment.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{ \"id\": 1, \"name\": \"ItemName\", \"description\": \"ItemDescription\", " +
                "\"available\": true, \"lastBooking\": { \"id\": 1, \"start\": \"2024-08-10T12:00:00\", \"end\": " +
                "\"2024-08-11T12:00:00\", \"status\": \"APPROVED\" }, \"nextBooking\": { \"id\": 2, \"start\": " +
                "\"2024-08-12T12:00:00\", \"end\": \"2024-08-13T12:00:00\", \"status\": \"APPROVED\" }, \"comments\": " +
                "[ { \"id\": 1, \"text\": \"Great item!\", \"authorName\": \"User1\", \"created\": \"2024-08-10T12:00:00\" } ] }";

        ObjectContent<ItemDtoWBookingAndComments> result = json.parse(content);

        assertThat(result).isEqualTo(new ItemDtoWBookingAndComments(
                1,
                "ItemName",
                "ItemDescription",
                true,
                new BookingDto(1, LocalDateTime.of(2024, 8, 10, 12, 0), LocalDateTime.of(2024, 8, 11, 12, 0), null, null, BookingStatus.APPROVED),
                new BookingDto(2, LocalDateTime.of(2024, 8, 12, 12, 0), LocalDateTime.of(2024, 8, 13, 12, 0), null, null, BookingStatus.APPROVED),
                List.of(new CommentDto(1, "Great item!", "User1", LocalDateTime.of(2024, 8, 10, 12, 0)))
        ));
    }
}