package ru.practicum.shareit.request.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * ДАО для {@link ItemRequest}.
 */
@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorIdOrderByCreationDateDesc(Long userId);

    List<ItemRequest> findByRequestorIdNot(Long userId, Pageable pageable);
}
