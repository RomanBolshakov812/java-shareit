package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNullException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer ownerId) {
        UserMapper.toUserDto(userRepository.findById(ownerId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + ownerId + " не найден!")));
        Request request;
        Integer requestId = itemDto.getRequestId();
        if (requestId != null) {
            request = requestRepository.findById(requestId).orElseThrow();
        } else {
            request = null;
        }
        Item item = ItemMapper.toItem(itemDto, ownerId, request);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer itemId, ItemDto itemDto, Integer ownerId) {
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() ->
        new EntityNullException("Вещь с id = " + itemId + " не найдена!"));
        if (!Objects.equals(updatedItem.getOwnerId(), ownerId)) {
            throw new NullObjectException("Неверный id владельца вещи!");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Integer ownerId) {
        List<Item> itemsByOwnerId = itemRepository.findAllItemsByOwnerIdOrderById(ownerId);
        List<Integer> itemsId = new ArrayList<>();
        for (Item item : itemsByOwnerId) {
            itemsId.add(item.getId());
        }
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Integer itemId : itemsId) {
            ItemDto itemDto = getItem(itemId, ownerId);
            itemsDto.add(itemDto);
        }
        return itemsDto;
    }

    @Override
    public ItemDto getItem(Integer itemId, Integer sharerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNullException("Вещь с id = " + itemId + " не найдена!"));
        Booking lastBooking = getLastBooking(itemId);
        Booking nextBooking = getNextBooking(itemId);
        List<Comment> comments = commentRepository.findAllCommentByItemId(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (Objects.equals(item.getOwnerId(), sharerId)) {
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDtoShort(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDtoShort(nextBooking));
            }
        }
        itemDto.setComments(CommentMapper.toListCommentDto(comments));
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> items = new ArrayList<>();
        if (text.isBlank()) {
            return items;
        }
        return ItemMapper.toListItemDto(itemRepository
                .findByDescriptionContainingIgnoreCaseAndAvailable(text, true));
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Integer itemId, Integer authorId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNullException("Вещь с id = " + itemId + " не найдена!"));
        User author = userRepository.findById(authorId).orElseThrow(() ->
                new EntityNullException("Пользователь с id = " + authorId + " не найден!"));
        isValid(itemId, authorId);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = CommentMapper.toComment(commentDto, item, author, created);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private Booking getLastBooking(Integer itemId) {
        Booking lastBooking = bookingRepository
                .findTopBookingByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now());
        if (lastBooking != null) {
            while (lastBooking.getStatus().equals(BookingState.REJECTED)) {
                LocalDateTime lastBookingStart = lastBooking.getStart();
                lastBooking = bookingRepository
                        .findTopBookingByItemIdAndStartBeforeOrderByStartDesc(itemId,
                                lastBookingStart);
                if (lastBooking == null) {
                    break;
                }
            }
        }
        return lastBooking;
    }

    private Booking getNextBooking(Integer itemId) {
        Booking nextBooking = bookingRepository
                .findTopBookingByItemIdAndStartAfterOrderByStart(itemId, LocalDateTime.now());
        if (nextBooking != null) {
            while (nextBooking.getStatus().equals(BookingState.REJECTED)) {
                LocalDateTime lastBookingStart = nextBooking.getStart();
                nextBooking = bookingRepository
                        .findTopBookingByItemIdAndStartBeforeOrderByStartDesc(itemId,
                                lastBookingStart);
                if (nextBooking == null) {
                    break;
                }
            }
        }
        return nextBooking;
    }

    private void isValid(Integer itemId, Integer bookerId) {
        List<LocalDateTime> endDatesBookings = bookingRepository.getListBookingEndDate(bookerId,
                itemId);
        if (endDatesBookings.size() == 0) {
            throw new ValidationException("У пользователя с id = " + bookerId
                    + " нет бронирований вещи с id = " + itemId + "!");
        }
        int completedBookingsCount = 0;
        for (LocalDateTime endDate : endDatesBookings) {
            if (endDate.isBefore(LocalDateTime.now())) {
                completedBookingsCount++;
            }
        }
        if (completedBookingsCount == 0) {
            throw new ValidationException("У пользователя с id = "
                    + bookerId + " нет завершенных бронирований вещи с id = " + itemId + "!");
        }
    }
}
