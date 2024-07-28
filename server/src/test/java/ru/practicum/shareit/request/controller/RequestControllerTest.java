package ru.practicum.shareit.request.controller;

import ru.practicum.shareit.request.service.RequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({RequestController.class})
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService service;

    @Test
    @DisplayName("Test create request")
    public void createRequestTest() throws Exception {
        String xSharerUserId = "1";
        String requestDTO = "{\"description\": \"Дайте пожалуйста отвёртку\"}";

        mockMvc.perform(post("/requests")
                        .content(requestDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get all requests")
    public void getAllRequestsTest() throws Exception {
        String xSharerUserId = "1";
        String from = "5";
        String size = "20";

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get request")
    public void getRequestTest() throws Exception {
        String xSharerUserId = "1";
        String requestId = "1";

        mockMvc.perform(
                        get("/requests/{requestId}", requestId)
                                .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get requests")
    public void getRequestsTest() throws Exception {
        String xSharerUserId = "1";

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
