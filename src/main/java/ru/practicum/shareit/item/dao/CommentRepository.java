package ru.practicum.shareit.item.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

/**
 * ДАО для {@link Comment}.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    List<Comment> findByItemIdIn(List<Long> itemIds);

    default Map<Long, List<Comment>> findByItemIds(List<Long> itemIds) {
        return findByItemIdIn(itemIds).stream().collect(
                Collectors.groupingBy(comment -> comment.getItem().getId()));
    }
}
