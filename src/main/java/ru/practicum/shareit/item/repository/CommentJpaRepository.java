package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.entity.CommentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByItemEntityId(Long itemId);

    List<CommentEntity> findAllByItemEntityIdIn(List<Long> itemIds);
}
