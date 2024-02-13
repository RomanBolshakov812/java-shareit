package ru.practicum.item;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "http://localhost:9090/items")
public class ItemControllerGateway {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemClient.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Integer itemId,
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemClient.updateItem(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Integer itemId,
                                          @RequestHeader("X-Sharer-User-Id") Integer sharerId) {
        return itemClient.getItem(itemId, sharerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemClient.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                    @PathVariable Integer itemId,
                                    @RequestHeader("X-Sharer-User-Id") Integer bookerId) {
        return itemClient.addComment(commentDto, itemId, bookerId);
    }
}
