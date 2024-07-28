package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.entity.UserEntity;
import ru.yandex.practicum.generated.model.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserDTO user);

    UserDTO updateUser(UserDTO user);

    UserDTO getUserById(Long userId);

    void deleteUser(Long userId);

    List<UserDTO> getAllUsers();

    UserEntity getUserEntity(Long userId);
}
