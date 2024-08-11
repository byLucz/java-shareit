package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWBooking;
import ru.practicum.shareit.item.dto.ItemDtoWBookingAndComments;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId);

    ItemDtoWBookingAndComments getItemByIdAndUserId(Integer userId, Integer itemId);

    Item getItemById(Integer itemId);

    List<ItemDtoWBooking> getAllUserItems(Integer userId);

    List<ItemDto> searchItem(String text);

    CommentDto postComment(CommentDto commentDto, Integer userId, Integer itemId);

}