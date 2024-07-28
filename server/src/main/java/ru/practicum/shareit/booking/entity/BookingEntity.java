package ru.practicum.shareit.booking.entity;

import ru.practicum.shareit.booking.entity.enumerated.Status;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString(exclude = {"itemEntity", "userEntity"})
@RequiredArgsConstructor
@Accessors(chain = true)
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @FutureOrPresent
    @Column(name = "start_date")
    private LocalDateTime start;

    @Future
    @Column(name = "end_date")
    private LocalDateTime end;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.DETACH})
    private ItemEntity itemEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 8)
    private Status status;

    @JoinColumn(name = "booker_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.DETACH})
    private UserEntity userEntity;
}
