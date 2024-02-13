package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private Integer itemId;
    private Integer userId;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemId = 1;
        userId = 1;
        itemDto = new ItemDto(null, "item1", "item1 description", true,
                null, null, null, null);
    }

    @SneakyThrows
    @Test
    void createItem_whenItemNotValid_thenReturnedBadRequest() {
        ItemDto notValidItem = new ItemDto();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(notValidItem)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(notValidItem, userId);
    }

    @SneakyThrows
    @Test
    void createItem_whenItemIsValid_then_returnedOkStatus() {
        when(itemService.addItem(itemDto, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        ItemDto newItemDto = new ItemDto(null, "New Name", null, null,
                null, null, null, null);
        itemDto.setName("New Name");
        when(itemService.updateItem(itemId, newItemDto, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void getItem() {
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItemsByOwnerId() {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).getItemsByOwnerId(userId);
    }

    @SneakyThrows
    @Test
    void searchItems() {
        mockMvc.perform(get("/items/search")
                        .param("text", "param"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(itemService).searchItem("param");
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentNotValid_thenReturnedBarRequest() {
        CommentDto notValidComment = new CommentDto();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(notValidComment)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(notValidComment, itemId, userId);
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentIsValid_thenReturnedOkStatus() {
        LocalDateTime create = LocalDateTime.parse("2023-04-01T01:01:01");
        CommentDto commentDto = new CommentDto(null, "text", itemDto, "Name",
                create);

        when(itemService.addComment(commentDto, itemId, userId)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }
}
