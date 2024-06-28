package ru.practicum.shareit.user.service;

import java.util.List;
import ru.yandex.practicum.generated.model.dto.UserDTO;

public interface UserService {

    UserDTO createUser(UserDTO user);

    UserDTO updateUser(UserDTO user);

    UserDTO getUserById(Long userId);

    void deleteUser(Long userId);

    List<UserDTO> getAllUsers();
}
