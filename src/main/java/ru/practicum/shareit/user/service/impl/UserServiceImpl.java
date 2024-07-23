package ru.practicum.shareit.user.service.impl;

import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.generated.model.dto.UserDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO user) {
        if (Objects.isNull(user.getEmail())) {
            throw new BadRequestException("Email не может быть пустым");
        }
        return userMapper.toDto(userRepository.save(userMapper.toEntity(user)));
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserDTO user) {
        UserEntity userEntity = getUserEntity(user.getId());
        userMapper.updateUserEntity(userEntity, user);
        return userMapper.toDto(userRepository.save(userEntity));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return userMapper.toDto(getUserEntity(userId));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь с id={} не найден", userId);
            return new NotFoundException("Пользователь с id " + userId + " не найден");
        });
    }
}
