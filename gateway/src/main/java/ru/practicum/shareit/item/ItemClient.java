package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Integer userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemByIdAndUserId(Integer userId, Integer itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllUserItems(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> postComment(Integer userId, Integer itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
