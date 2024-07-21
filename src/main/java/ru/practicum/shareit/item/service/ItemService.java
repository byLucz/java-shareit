package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getAllItems(Integer userId);

    List<ItemDto> searchItems(String text);

    void deleteItem(Integer userId, Integer itemId);
}