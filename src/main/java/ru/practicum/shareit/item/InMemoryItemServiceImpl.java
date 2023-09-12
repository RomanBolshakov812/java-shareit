package ru.practicum.shareit.item;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

@Service
@RequiredArgsConstructor
public class InMemoryItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer ownerId) {
        isValid(itemDto);
        if (userStorage.getUser(ownerId) == null) {
            throw new NullObjectException("Пользователь с id = " + ownerId + " не найден!");
        }
        return itemStorage.createItem(itemDto, ownerId);
    }

    @Override
    public ItemDto updateItem(Integer itemId, ItemDto itemDto, Integer ownerId) {
        Item updatedItem = itemStorage.getItem(itemId);
        Item currentItem = ItemMapper.toItem(itemDto, itemId, ownerId);
        if (!Objects.equals(updatedItem.getOwnerId(), ownerId)) {
            throw new NullObjectException("Неверный id владельца вещи!");
        }
        if (currentItem.getName() != null) {
            updatedItem.setName(currentItem.getName());
        }
        if (currentItem.getDescription() != null) {
            updatedItem.setDescription(currentItem.getDescription());
        }
        if (currentItem.getAvailable() != null) {
            updatedItem.setAvailable(currentItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(updatedItem));
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Integer ownerId) {
        return itemStorage.getItemsByOwnerId(ownerId);
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = itemStorage.getItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemStorage.searchItem(text);
    }

    private  void isValid(ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException("Отсутствуют данные создаваемой вещи!");
        } else if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Отсутствует название вещи!");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Отсутствует описание вещи!");
        } else if (itemDto.getAvailable() == null) {
            throw new ValidationException("Отсутствует статус вещи!");
        }
    }
}
