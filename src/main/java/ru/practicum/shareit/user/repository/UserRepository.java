package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.entity.UserEntity;
import java.util.List;

public interface UserRepository {

    UserEntity createUser(UserEntity user);

    UserEntity updateUser(UserEntity user);

    UserEntity getUserById(Long userId);

    void deleteUser(Long userId);

    List<UserEntity> getAllUsers();
}
