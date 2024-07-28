package ru.practicum.shareit.user.service.impl;

import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.generated.model.dto.UserDTO;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl test")
class UserServiceImplTest {

    @Mock
    private UserJpaRepository repository;

    @Spy
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDTO userDtoStub = new UserDTO()
            .email("test@mail.ru")
            .name("test name");
    private final UserEntity userEntityAfterCreate = new UserEntity()
            .setId(1L)
            .setName(userDtoStub.getName())
            .setEmail(userDtoStub.getEmail());
    private final UserDTO userDtoAfterCreate = new UserDTO()
            .id(1L)
            .name(userDtoStub.getName())
            .email(userDtoStub.getEmail());

    @Test
    @DisplayName("Test create user")
    void createUser() {
        Mockito.when(repository.save(Mockito.any(UserEntity.class)))
                .thenReturn(userEntityAfterCreate);

        UserDTO user = userService.createUser(userDtoStub);

        Assertions.assertThat(user)
                .isEqualTo(userDtoAfterCreate);
    }

    @Test
    @DisplayName("Test create without email")
    void createWithoutEmail() {
        UserDTO userDto = new UserDTO()
                .name("test name");
        Assertions.assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email не может быть пустым");
    }

    @Test
    @DisplayName("Test update user")
    void updateUser() {
        UserDTO updatedUserDto = new UserDTO()
                .id(1L)
                .email("new@mail.ru")
                .name("new name");
        UserEntity updatedUserEntity = new UserEntity()
                .setId(1L)
                .setEmail(updatedUserDto.getEmail())
                .setName(updatedUserDto.getName());

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(userEntityAfterCreate));

        Mockito.when(repository.save(Mockito.any(UserEntity.class)))
                .thenReturn(updatedUserEntity);

        UserDTO userDTO = userService.updateUser(updatedUserDto);

        Assertions.assertThat(userDTO)
                .isEqualTo(updatedUserDto);
    }

    @Test
    @DisplayName("Test get user by id")
    void getUserById() {
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(userEntityAfterCreate));

        UserDTO userDTO = userService.getUserById(1L);

        Assertions.assertThat(userDTO)
                .isEqualTo(userDtoAfterCreate);
    }

    @Test
    @DisplayName("Test get user by id not found")
    void getUserByIdNotFound() {
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id 1 не найден");
    }

    @Test
    @DisplayName("Test delete user")
    void deleteUser() {
        userService.deleteUser(1L);

        Mockito.verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Test get all users")
    void getAllUsers() {
        Mockito.when(repository.findAll())
                .thenReturn(List.of(userEntityAfterCreate));

        List<UserDTO> users = userService.getAllUsers();

        Assertions.assertThat(users)
                .isEqualTo(List.of(userDtoAfterCreate));
    }

    @Test
    @DisplayName("Test get user entity")
    void getUserEntity() {
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(userEntityAfterCreate));

        UserEntity userEntity = userService.getUserEntity(1L);

        Assertions.assertThat(userEntity)
                .isEqualTo(userEntityAfterCreate);
    }
}