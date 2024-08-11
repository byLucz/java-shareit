package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        User user = new User(1, "TestUser", "testuser@example.com");
        LocalDateTime created = LocalDateTime.of(2024, 8, 10, 12, 0, 0);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Request Description", created, user);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).hasJsonPathMapValue("$.requester");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.requester.id").isEqualTo(user.getId());
        assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.requester.email").isEqualTo(user.getEmail());
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"description\":\"Request Description\",\"created\":\"2024-08-10T12:00:00\"," +
                "\"requester\":{\"id\":1,\"name\":\"TestUser\",\"email\":\"testuser@example.com\"}}";

        ObjectContent<ItemRequestDto> result = json.parse(content);

        LocalDateTime expectedCreated = LocalDateTime.of(2024, 8, 10, 12, 0, 0);
        User expectedUser = new User(1, "TestUser", "testuser@example.com");

        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getDescription()).isEqualTo("Request Description");
        assertThat(result.getObject().getCreated()).isEqualTo(expectedCreated);
        assertThat(result.getObject().getRequester().getId()).isEqualTo(expectedUser.getId());
        assertThat(result.getObject().getRequester().getName()).isEqualTo(expectedUser.getName());
        assertThat(result.getObject().getRequester().getEmail()).isEqualTo(expectedUser.getEmail());
    }
}
