package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Optional<Item> getItemById(Integer itemId);

    List<Item> findItemByOwnerIdOrderById(Integer ownerId);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailable(String text, boolean available);
}
