package ru.practicum.shareit.item.entity;

import ru.practicum.shareit.request.entity.RequestEntity;
import ru.practicum.shareit.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Entity
@Table(name = "items")
@RequiredArgsConstructor
@Accessors(chain = true)
@ToString(exclude = {"owner", "requestEntity"})
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private String description;

    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.DETACH})
    @JoinColumn(name = "request_entity_id")
    private RequestEntity requestEntity;

}
