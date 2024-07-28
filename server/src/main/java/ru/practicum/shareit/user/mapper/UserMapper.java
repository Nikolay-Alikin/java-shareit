package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.generated.model.dto.UserDTO;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = ComponentModel.SPRING)
public interface UserMapper {

    UserEntity toEntity(UserDTO dto);

    UserDTO toDto(UserEntity entity);

    List<UserDTO> toDtoList(List<UserEntity> entities);

    List<UserEntity> toEntityList(List<UserDTO> dtos);

    default void updateUserEntity(UserEntity entity, UserDTO dto) {
        entity.setName(dto.getName() == null ? entity.getName() : dto.getName());
        entity.setEmail(dto.getEmail() == null ? entity.getEmail() : dto.getEmail());
    }
}
