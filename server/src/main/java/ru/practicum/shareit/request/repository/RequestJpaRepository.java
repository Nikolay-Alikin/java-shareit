package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestJpaRepository extends JpaRepository<RequestEntity, Long> {

    List<RequestEntity> findAllByRequestorIdOrderByCreatedAsc(Long requestorId);



}
