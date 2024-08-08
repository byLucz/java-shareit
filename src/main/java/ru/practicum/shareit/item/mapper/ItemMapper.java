package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWBooking;
import ru.practicum.shareit.item.dto.ItemDtoWBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor

public class ItemMapper {

    private final BookingMapper bookingMapper;

    public Item toItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public ItemDtoWBooking toItemDtoWBooking(Item item, Booking lastBooking, Booking nextBooking) {
        ItemDtoWBooking itemDto = new ItemDtoWBooking();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(lastBooking != null ? bookingMapper.toBookingDto(lastBooking) : null);
        itemDto.setNextBooking(nextBooking != null ? bookingMapper.toBookingDto(nextBooking) : null);
        return itemDto;
    }

    public ItemDtoWBookingAndComments toItemDtoWithBookingAndComments(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemDtoWBookingAndComments itemDto = new ItemDtoWBookingAndComments();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(lastBooking != null ? bookingMapper.toBookingDto(lastBooking) : null);
        itemDto.setNextBooking(nextBooking != null ? bookingMapper.toBookingDto(nextBooking) : null);
        itemDto.setComments(comments.stream().map(this::toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public Comment toComment(CommentDto commentDto, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setUser(user);
        comment.setItem(item);
        return comment;
    }
}
