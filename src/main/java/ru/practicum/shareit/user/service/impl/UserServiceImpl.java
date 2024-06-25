package ru.practicum.shareit.user.service.impl;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.generated.model.dto.UserDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserDTO user) {
        return userMapper.toDto(userRepository.createUser(userMapper.toEntity(user)));
    }

    @Override
    public UserDTO updateUser(UserDTO user) {
        return userMapper.toDto(userRepository.updateUser(userMapper.toEntity(user)));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        if (userRepository.getUserById(userId) != null) {
            return userMapper.toDto(userRepository.getUserById(userId));
        }
        log.error("User with id {} not found", userId);
        throw new NotFoundException("User with id " + userId + " not found");
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.toDtoList(userRepository.getAllUsers());
    }
}
