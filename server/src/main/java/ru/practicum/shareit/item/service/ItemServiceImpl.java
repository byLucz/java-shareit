package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exceptions.CommentServiceException;
import ru.practicum.shareit.exceptions.ItemServiceException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoWBookingAndComments;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.repo.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemMapper.toItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
            item.setRequest(request);
        }
        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с айди " + itemId + " не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemServiceException("Пользователь не владелец вещи");
        }
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    public ItemDtoWBookingAndComments getItemByIdAndUserId(Integer userId, Integer itemId) {
        Optional<User> user = userRepository.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с айди " + itemId + " не найдена"));

        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findLastBookingByItemId(itemId).orElse(null);
            nextBooking = bookingRepository.findNextBookingByItemId(itemId).orElse(null);
        }

        return itemMapper.toItemDtoWithBookingAndComments(item, lastBooking, nextBooking, comments);
    }

    @Transactional(readOnly = true)
    public List<ItemDtoWBooking> getAllUserItems(Integer userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Integer> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);

        Map<Integer, Booking> lastBookings = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking, (existing, replacement) -> existing.getEnd().isAfter(replacement.getEnd()) ? existing : replacement));

        Map<Integer, Booking> nextBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking, (existing, replacement) -> existing.getStart().isBefore(replacement.getStart()) ? existing : replacement));

        return items.stream()
                .map(item -> itemMapper.toItemDtoWBooking(item, lastBookings.get(item.getId()), nextBookings.get(item.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Item getItemById(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с айди " + itemId + " не найдена"));
    }

    @Transactional
    public CommentDto postComment(CommentDto commentDto, Integer userId, Integer itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = getItemById(itemId);

        if (commentDto.getText().isBlank()) {
            throw new ItemServiceException("Текст отзыва не может быть пустым");
        }

        boolean hasBooking = bookingRepository.findFirst1ByItemIdAndBookerIdAndEndIsBefore(itemId, userId, LocalDateTime.now()).isPresent();
        if (!hasBooking) {
            throw new CommentServiceException("Нельзя оставить отзыв без использования");
        }

        Comment comment = CommentMapper.toComment(commentDto, user, item);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
}