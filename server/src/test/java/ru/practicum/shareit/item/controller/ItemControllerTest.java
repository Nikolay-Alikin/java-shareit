package ru.practicum.shareit.item.controller;

import ru.practicum.shareit.item.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemController.class})
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    @DisplayName("Test create comment")
    public void createCommentTest() throws Exception {
        String xSharerUserId = "1";
        String itemId = "1";
        String commentRequestDTO = "{\"serialVersionUID\": \"\", \"text\": \"Какой-то комментарий\"}";

        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .content(commentRequestDTO)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create item")
    public void createItemTest() throws Exception {
        String xSharerUserId = "1";
        String itemDTO = "{\"serialVersionUID\": \"\", \"id\": \"1\", \"name\": \"Дрель\", \"description\": \"Ручная\", "
                         + "\"available\": \"true\", \"request\": \"\", \"lastBooking\": {}, \"nextBooking\": {}, "
                         + "\"comments\": []}";

        mockMvc.perform(post("/items")
                        .content(itemDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create item name is null")
    public void createItemNameIsNullTest() throws Exception {
        String xSharerUserId = "1";
        String itemDTO = "{\"serialVersionUID\": \"\", \"id\": \"1\", \"description\": \"Ручная\", \"available\": "
                         + "\"true\", \"request\": \"\", \"lastBooking\": {}, \"nextBooking\": {}, \"comments\": []}";

        mockMvc.perform(post("/items")
                        .content(itemDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create item description is null")
    public void createItemDescriptionIsNullTest() throws Exception {
        String xSharerUserId = "1";
        String itemDTO = "{\"serialVersionUID\": \"\", \"id\": \"1\", \"name\": \"Дрель\", \"available\": \"true\", "
                         + "\"request\": \"\", \"lastBooking\": {}, \"nextBooking\": {}, \"comments\": []}";

        mockMvc.perform(post("/items")
                        .content(itemDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create item name is blank")
    public void createItemNameIsBlankTest() throws Exception {
        String xSharerUserId = "1";
        String itemDTO = "{\"serialVersionUID\": \"\", \"id\": \"1\", \"name\": \"\", \"description\": \"Ручная\", "
                         + "\"available\": \"true\", \"request\": \"\", \"lastBooking\": {}, \"nextBooking\": {}, "
                         + "\"comments\": []}";

        mockMvc.perform(post("/items")
                        .content(itemDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create item description is blank")
    public void createItemDescriptionIsBlankTest() throws Exception {
        String xSharerUserId = "1";
        String itemDTO = "{\"serialVersionUID\": \"\", \"id\": \"1\", \"name\": \"Дрель\", \"description\": \"\", "
                         + "\"available\": \"true\", \"request\": \"\", \"lastBooking\": {}, \"nextBooking\": {}, "
                         + "\"comments\": []}";

        mockMvc.perform(post("/items")
                        .content(itemDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get all items")
    public void getAllItemsTest() throws Exception {
        String xSharerUserId = "1";

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get item")
    public void getItemTest() throws Exception {
        String xSharerUserId = "1";
        String itemId = "1";

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test search items")
    public void searchItemsTest() throws Exception {
        String xSharerUserId = "1";
        String text = "\"дрель\"";

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test update item")
    public void updateItemTest() throws Exception {
        String xSharerUserId = "1";
        String itemId = "1";
        String itemDTO = "{\"serialVersionUID\": \"\", \"id\": \"1\", \"name\": \"Дрель\", \"description\": "
                         + "\"Автоматическая\", \"available\": \"true\", \"request\": \"\", \"lastBooking\": {}, "
                         + "\"nextBooking\": {}, \"comments\": []}";

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .content(itemDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
