package ru.practicum.shareit.item;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllItemsByOwnerIdOrderById(Integer ownerId);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailable(String text, boolean available);

    List<Item> findItemByRequestId(Integer requestId);
}
