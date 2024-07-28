package ru.practicum.shareit.user.controller;

import ru.practicum.shareit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.generated.api.UsersApi;
import ru.yandex.practicum.generated.model.dto.UserDTO;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserDTO> createUser(UserDTO userDTO) {
        log.info("Поступил запрос на создание пользователя: {}", userDTO);
        UserDTO user = userService.createUser(userDTO);
        log.info("Пользователь создан: {}", user);
        return ResponseEntity.status(200).body(user);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        log.info("Поступил запрос на удаление пользователя: {}", userId);
        userService.deleteUser(userId);
        log.info("Пользователь удален: {}", userId);
        return ResponseEntity.status(204).build();
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Поступил запрос на получение всех пользователей");
        List<UserDTO> users = userService.getAllUsers();
        log.info("Получены пользователи: {}", users);
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserDTO> getUser(Long userId) {
        log.info("Поступил запрос на получение пользователя: {}", userId);
        UserDTO user = userService.getUserById(userId);
        log.info("Получен пользователь: {}", user);
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(Long userId, UserDTO userDTO) {
        log.info("Поступил запрос на обновление пользователя: {}", userId);
        userDTO.setId(userId);
        UserDTO user = userService.updateUser(userDTO);
        log.info("Пользователь обновлен: {}", user);
        return ResponseEntity.ok(user);
    }
}
