package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

@Service
@AllArgsConstructor
public class InMemoryItemServiceImpl implements ItemService {

    private ItemStorage itemStorage;
    private UserStorage userStorage;

    @Override
    public Item addItem(ItemDto itemDto, Integer ownerId) {
        if (userStorage.getUser(ownerId) == null) {
            throw new NullObjectException("Пользователь с id = " + ownerId + " не найден!");
        }
        isValid(itemDto, ownerId);
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(ownerId);
        item.setRequest(null);
        itemStorage.createItem(item);
        return item;
    }

    @Override
    public Item updateItem(Integer itemId, Item item, Integer ownerId) {
        Item currentItem = itemStorage.getItem(itemId);
        if (!Objects.equals(currentItem.getOwnerId(), ownerId)) {
            throw new NullObjectException("Неверный id владельца вещи!");
        }
        if (item.getName() != null) {
            currentItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            currentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            currentItem.setAvailable(item.getAvailable());
        }
        return itemStorage.updateItem(currentItem);
    }

    @Override
    public void deleteItem(Integer itemId) {
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Integer ownerId) {
        List<ItemDto> itemsOfOwner = new ArrayList<>();
        for (Item item : itemStorage.getAllItems()) {
            if (Objects.equals(item.getOwnerId(), ownerId)) {
                itemsOfOwner.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsOfOwner;
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = itemStorage.getItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        String textInLowerCase = text.toLowerCase();
        List<Item> items = itemStorage.getAllItems();
        List<ItemDto> foundItems = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : items) {
                if (item.getName().toLowerCase().contains(textInLowerCase)
                        || item.getDescription().toLowerCase().contains(textInLowerCase)) {
                    if (item.getAvailable()) {
                        foundItems.add(ItemMapper.toItemDto(item));
                    }
                }
            }
        }
        return foundItems;
    }

    private  void isValid(ItemDto itemDto, Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("Отсутствует id владельца вещи!");
        } else if (itemDto == null) {
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
