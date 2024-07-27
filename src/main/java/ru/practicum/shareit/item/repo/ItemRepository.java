package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerId(Integer ownerId);
    List<Item> findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
