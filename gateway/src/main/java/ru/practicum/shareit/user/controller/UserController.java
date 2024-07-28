package ru.practicum.shareit.user.controller;

import ru.practicum.shareit.user.feign.UserClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.generated.api.UsersApi;
import ru.yandex.practicum.generated.model.dto.UserDTO;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserClient client;

    @Override
    public ResponseEntity<UserDTO> createUser(UserDTO userDTO) {
        log.info("Поступил запрос на создание пользователя: {}", userDTO);
        ResponseEntity<UserDTO> response = client.createUser(userDTO);
        log.info("Пользователь создан: {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        log.info("Поступил запрос на удаление пользователя: {}", userId);
        client.deleteUser(userId);
        log.info("Пользователь удален: {}", userId);
        return ResponseEntity.status(204).build();
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Поступил запрос на получение всех пользователей");
        ResponseEntity<List<UserDTO>> response = client.getAllUsers();
        log.info("Получены пользователи: {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<UserDTO> getUser(Long userId) {
        log.info("Поступил запрос на получение пользователя: {}", userId);
        ResponseEntity<UserDTO> response = client.getUser(userId);
        log.info("Получен пользователь: {}", response.getBody());
        return response;
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(Long userId, UserDTO userDTO) {
        log.info("Поступил запрос на обновление пользователя: {}", userId);
        ResponseEntity<UserDTO> response = client.updateUser(userId, userDTO);
        log.info("Пользователь обновлен: {}", response.getBody());
        return response;
    }
}