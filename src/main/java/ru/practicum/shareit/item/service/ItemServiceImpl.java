package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemServiceException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        Item item = ItemMapper.toItem(itemDto, user);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = checkItem(itemId, userId);
        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = checkItem(itemId, null);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems(Integer userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Integer userId, Integer itemId) {
        Item item = checkItem(itemId, userId);
        itemRepository.deleteById(item.getId());
    }

    private Item checkItem(Integer itemId, Integer userId) {
        var errMessage = "Предмет с id %d не существует%s";
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ItemServiceException(String.format(errMessage, itemId, ""));
        }
        Item item = itemOptional.get();
        if (userId != null && !item.getOwner().getId().equals(userId)) {
            throw new ItemServiceException(String.format(errMessage, itemId, " у пользователя с id " + userId));
        }
        return item;
    }
}