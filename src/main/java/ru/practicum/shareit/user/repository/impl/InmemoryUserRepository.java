package ru.practicum.shareit.user.repository.impl;

import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InmemoryUserRepository implements UserRepository {

    private final Map<Long, UserEntity> users = new HashMap<>();
    private final Map<Long, String> emails = new HashMap<>();

    private long count = 0;


    @Override
    public UserEntity createUser(UserEntity user) {
        if (user.getEmail() == null) {
            throw new BadRequestException("Email cannot be null");
        }
        emailValidation(user);
        user.setId(++count);
        users.put(count, user);
        emails.put(user.getId(), user.getEmail());
        return users.get(user.getId());
    }

    @Override
    public UserEntity updateUser(UserEntity updateUser) {
        emailValidation(updateUser);
        if (users.containsKey(updateUser.getId())) {
            UserEntity userForUpdate = users.get(updateUser.getId());
            merge(userForUpdate, updateUser);
            users.put(userForUpdate.getId(), userForUpdate);
            emails.put(userForUpdate.getId(), userForUpdate.getEmail());
            return users.get(userForUpdate.getId());
        }
        return createUser(updateUser);
    }

    @Override
    public UserEntity getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }
        return users.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
        emails.remove(userId);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void emailValidation(UserEntity user) {
        String email = user.getEmail();
        if (user.getId() != null && emails.get(user.getId()).equals(email)) {
            return;
        }
        if (emails.containsValue(email)) {
            throw new ConflictException("Пользователь с электронной почтой" + email + "уже существует");
        }
    }

    private void merge(UserEntity userForUpdate, UserEntity updatedUser) {
        String updatedUserName = updatedUser.getName();
        String updatedEmail = updatedUser.getEmail();

        userForUpdate.setEmail(updatedEmail == null ? userForUpdate.getEmail() : updatedEmail);
        userForUpdate.setName(updatedUserName == null ? userForUpdate.getName() : updatedUserName);
    }
}