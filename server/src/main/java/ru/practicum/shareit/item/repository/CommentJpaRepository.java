package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByItemEntityId(Long itemId);

    List<CommentEntity> findAllByItemEntityIdIn(List<Long> itemIds);
}
