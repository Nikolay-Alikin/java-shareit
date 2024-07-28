package ru.practicum.shareit.user.controller;

import ru.practicum.shareit.user.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.generated.model.dto.UserDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Test create user")
    public void createUserTest() throws Exception {
        String userDTO = "{\"serialVersionUID\": \"\", \"id\": \"\", \"name\": \"Test user\", \"email\": \"test@mail.ru\"}";

        mockMvc.perform(post("/users")
                        .content(userDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test create user -> throws ConstraintViolationException")
    public void createUserThrowsConstraintViolationExceptionTest() throws Exception {
        String userDTO = "{\"serialVersionUID\": \"\", \"id\": \"\", \"name\": \"Test user\", \"email\": \"test@mail.ru\"}";
        Mockito.when(userService.createUser(Mockito.any(UserDTO.class)))
                .thenThrow(new ConstraintViolationException("message", null));

        mockMvc.perform(post("/users")
                        .content(userDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get all users")
    public void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test delete user")
    public void deleteUserTest() throws Exception {
        String userId = "1";

        mockMvc.perform(
                        delete("/users/{userId}", userId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("Test get user")
    public void getUserTest() throws Exception {
        String userId = "1";

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Test update user")
    public void updateUserTest() throws Exception {
        String userId = "1";
        String userDTO = "{\"serialVersionUID\": \"\", \"id\": \"\", \"name\": \"Test user\", \"email\": \"test@mail.ru\"}";

        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(userDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
