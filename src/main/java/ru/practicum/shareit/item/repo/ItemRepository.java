package ru.practicum.shareit.item.repo;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {

    private final Map<Integer, Item> itemMap = new HashMap<>();
    private int generatorId = 1;

    public Item save(final Item item) {
        if (item.getId() != null && itemMap.containsKey(item.getId())) {
            itemMap.put(item.getId(), item);
        } else {
            item.setId(generatorId++);
            itemMap.put(item.getId(), item);
        }
        return item;
    }

    public Optional<Item> findById(Integer itemId) {
        return Optional.ofNullable(itemMap.get(itemId));
    }

    public void deleteById(Integer itemId) {
        itemMap.remove(itemId);
    }

    public List<Item> findAllByOwnerId(Integer ownerId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Item> searchByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemMap.values().stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }
}