package ru.practicum.shareit.item.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

/**
 * ДАО для {@link Item}.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);

    @Query(" select item from Item item " +
            "where item.available = true and (lower(item.name) like lower(concat('%', ?1, '%')) " +
            " or lower(item.description) like lower(concat('%', ?1, '%')))")
    List<Item> search(String text, Pageable pageable);

    List<Item> findByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> requestIds);

    default Map<Long, List<Item>> findAllItemsByRequestIds(List<Long> requestIds) {
        return findByRequestIdIn(requestIds).stream().collect(
                Collectors.groupingBy(item -> item.getRequest().getId()));
    }
}
