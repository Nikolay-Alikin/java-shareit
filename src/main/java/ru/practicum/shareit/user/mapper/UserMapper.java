package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.entity.UserEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.generated.model.dto.UserDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = ComponentModel.SPRING)
public interface UserMapper {

    UserEntity toEntity(UserDTO dto);

    UserDTO toDto(UserEntity entity);

    List<UserDTO> toDtoList(List<UserEntity> entities);

    List<UserEntity> toEntityList(List<UserDTO> dtos);
}
