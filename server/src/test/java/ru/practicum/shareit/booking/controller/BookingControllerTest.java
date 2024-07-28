package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.booking.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({BookingController.class})
@ActiveProfiles("test")
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;


    @Test
    @DisplayName("Test approve booking")
    public void approveBookingTest() throws Exception {
        String xSharerUserId = "1";
        String bookingId = "1";
        String approved = "true";

        mockMvc.perform(patch("/bookings/{bookingId}",
                        bookingId)
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .param("approved", approved))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create booking")
    public void createBookingTest() throws Exception {
        String xSharerUserId = "1";
        LocalDateTime now = LocalDateTime.now();
        String bookingRequestDTO = String.format(
                "{\"serialVersionUID\": \"\", \"itemId\": \"1\", \"start\": \"%s\", \"end\": \"%s\"}", now,
                now.plusDays(1));

        mockMvc.perform(post("/bookings")
                        .content(bookingRequestDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create booking start before end")
    public void createBookingStartBeforeEndTest() throws Exception {
        String xSharerUserId = "1";
        LocalDateTime now = LocalDateTime.now();
        String bookingRequestDTO = String.format(
                "{\"serialVersionUID\": \"\", \"itemId\": \"1\", \"start\": \"%s\", \"end\": \"%s\"}", now,
                now.minusDays(5));

        mockMvc.perform(post("/bookings")
                        .content(bookingRequestDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create booking start equals end")
    public void createBookingStartEqualsEndTest() throws Exception {
        String xSharerUserId = "1";
        LocalDateTime now = LocalDateTime.now();
        String bookingRequestDTO = String.format(
                "{\"serialVersionUID\": \"\", \"itemId\": \"1\", \"start\": \"%s\", \"end\": \"%s\"}", now,
                now);

        mockMvc.perform(post("/bookings")
                        .content(bookingRequestDTO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get booking")
    public void getBookingTest() throws Exception {
        String xSharerUserId = "1";
        String bookingId = "1";

        mockMvc.perform(
                        get("/bookings/{bookingId}", bookingId)
                                .header("X-Sharer-User-Id", xSharerUserId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get bookings")
    public void getBookingsTest() throws Exception {
        String xSharerUserId = "1";
        String state = "ALL";

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get bookings with Unknown state")
    public void getBookingsWithUnknownStateTest() throws Exception {
        String xSharerUserId = "1";
        String state = "STATE";

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .param("state", state))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get owner bookings")
    public void getOwnerBookingsTest() throws Exception {
        String xSharerUserId = "1";
        String state = "ALL";

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
