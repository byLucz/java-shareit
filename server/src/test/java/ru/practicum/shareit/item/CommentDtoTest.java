package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 8, 10, 12, 0, 0);
        CommentDto commentDto = new CommentDto(1, "Test comment", "Test author", created);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).hasJsonPathStringValue("$.authorName");
        assertThat(result).hasJsonPathStringValue("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"text\":\"Test comment\",\"authorName\":\"Test author\",\"created\":\"2024-08-10T12:00:00\"}";

        ObjectContent<CommentDto> result = json.parse(content);

        LocalDateTime expectedCreated = LocalDateTime.of(2024, 8, 10, 12, 0, 0);

        assertThat(result).isEqualTo(new CommentDto(1, "Test comment", "Test author", expectedCreated));
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getText()).isEqualTo("Test comment");
        assertThat(result.getObject().getAuthorName()).isEqualTo("Test author");
        assertThat(result.getObject().getCreated()).isEqualTo(expectedCreated);
    }
}
