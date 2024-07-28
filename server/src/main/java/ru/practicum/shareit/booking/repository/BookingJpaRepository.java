package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.entity.enumerated.Status;
import ru.practicum.shareit.item.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByUserEntityId(Long userId);

    List<BookingEntity> findAllByUserEntityIdAndStatusIs(Long id, Status status);

    List<BookingEntity> findBookingsByUserEntityIdAndEndAfter(Long userEntityId, LocalDateTime end);

    List<BookingEntity> findBookingsByUserEntityIdAndEndBefore(Long userEntityId, LocalDateTime end);

    List<BookingEntity> findBookingsByUserEntityIdAndStartBeforeAndEndAfter(Long userEntityId, LocalDateTime start,
            LocalDateTime end);

    List<BookingEntity> findAllByItemEntityOwnerIdAndStartBeforeAndEndAfter(Long ownerId,
            LocalDateTime start, LocalDateTime end);

    List<BookingEntity> findAllByItemEntityOwnerIdAndEndBefore(Long userId, LocalDateTime end);

    List<BookingEntity> findAllByItemEntityOwnerIdAndEndAfter(Long ownerId, LocalDateTime end);

    List<BookingEntity> findAllByItemEntityOwnerIdAndStatusIs(Long ownerId, Status status);

    List<BookingEntity> findAllByItemEntityOwnerId(Long ownerId);

    @Query("select b "
           + "from BookingEntity as b "
           + "where b.itemEntity.id =:itemId "
           + "and b.start < current timestamp "
           + "order by b.end desc "
           + "limit 1")
    Optional<BookingEntity> findLastBooking(@Param("itemId") Long itemId);

    @Query("select b "
           + "from BookingEntity as b "
           + "where b.itemEntity.id =:itemId "
           + "and b.start > current timestamp "
           + "and b.status = 'APPROVED' "
           + "order by b.start asc "
           + "limit 1"
    )
    Optional<BookingEntity> findNextBooking(@Param("itemId") Long itemId);

    @Query("select b "
           + "from BookingEntity as b "
           + "where b.itemEntity.id in :itemIds "
           + "and b.start < current timestamp "
           + "order by b.end desc "
           + "limit 1")
    Optional<List<BookingEntity>> findLastBookings(@Param("itemIds") List<Long> itemIds);

    @Query("select b "
           + "from BookingEntity as b "
           + "where b.itemEntity.id in :itemIds "
           + "and b.start > current timestamp "
           + "and b.status = 'APPROVED' "
           + "order by b.start asc "
           + "limit 1"
    )
    Optional<List<BookingEntity>> findNextBookings(@Param("itemIds") List<Long> itemIds);

    boolean existsByItemEntityIdAndUserEntityIdAndStatusIsAndEndBefore(Long itemId, Long userId, Status status,
            LocalDateTime now);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BookingEntity b " +
           "WHERE b.itemEntity = :itemEntity AND " +
           "((b.start < :end AND b.end > :start) OR " +
           "(b.start BETWEEN :start AND :end) OR " +
           "(b.end BETWEEN :start AND :end))")
    boolean existsByItemEntityAndTimeConflict(
            @Param("itemEntity") ItemEntity itemEntity,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
