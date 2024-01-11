package ru.practicum.shareit.item.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

/**
 * ДАО для {@link Item}.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long userId);

    @Query(" select item from Item item " +
            "where item.available = true and (lower(item.name) like lower(concat('%', ?1, '%')) " +
            " or lower(item.description) like lower(concat('%', ?1, '%')))")
    List<Item> search(String text);
}
