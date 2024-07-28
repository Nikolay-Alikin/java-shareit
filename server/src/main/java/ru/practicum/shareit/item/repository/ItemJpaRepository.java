package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long>, JpaSpecificationExecutor<ItemEntity> {

    List<ItemEntity> findAllByOwnerId(Long id);

    List<ItemEntity> findItemEntitiesByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name,
            String description);

    List<ItemEntity> findItemEntitiesByRequestEntityId(Long requestEntityId);

    List<ItemEntity> findItemEntitiesByRequestEntityIdIn(Set<Long> requestEntityIds);
}