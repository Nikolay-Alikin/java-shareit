package ru.practicum.shareit.item.entity;

import ru.practicum.shareit.user.entity.UserEntity;
import lombok.Data;

@Data
public class ItemEntity {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserEntity owner;
    private String request;
}