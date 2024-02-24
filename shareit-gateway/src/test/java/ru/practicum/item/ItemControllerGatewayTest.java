package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

@WebMvcTest(ItemControllerGateway.class)
class ItemControllerGatewayTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemClient itemClient;
    private Integer itemId;
    private Integer userId;
    private ItemDto itemDto;
    private ResponseEntity<Object> response;

    @BeforeEach
    void beforeEach() {
        itemId = 1;
        userId = 1;
        itemDto = new ItemDto(null, "item1", "item1 description", true,
                null, null, null, null);
        response = new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @SneakyThrows
    @Test
    void createItemTest() {
        when(itemClient.createItem(itemDto, userId)).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemClient).createItem(itemDto, userId);
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        ItemDto newItemDto = new ItemDto(null, "New Name", null, null,
                null, null, null, null);
        itemDto.setName("New Name");
        when(itemClient.updateItem(itemId, newItemDto, userId)).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemClient).updateItem(itemId, newItemDto, userId);
    }

    @SneakyThrows
    @Test
    void getItemTest() {
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemClient).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItemsByOwnerIdTest() {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemClient).getItemsByOwnerId(userId);
    }

    @SneakyThrows
    @Test
    void searchItemsTest() {
        mockMvc.perform(get("/items/search")
                        .param("text", "param"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemClient).searchItems("param");
    }

    @SneakyThrows
    @Test
    void createCommentTest() {
        LocalDateTime create = LocalDateTime.parse("2023-04-01T01:01:01");
        CommentDto commentDto = new CommentDto(null, "text", itemDto, "Name",
                create);
        ResponseEntity<Object> commentResponse = new ResponseEntity<>(commentDto, HttpStatus.OK);

        when(itemClient.addComment(commentDto, itemId, userId)).thenReturn(commentResponse);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemClient).addComment(commentDto, itemId, userId);
    }
}
