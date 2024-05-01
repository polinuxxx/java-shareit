package ru.practicum.shareit.item.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.base.AbstractEntity;
import ru.practicum.shareit.user.model.User;

/**
 * Отзыв.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"item", "author"})
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments")
public class Comment extends AbstractEntity {

    String text;

    @ManyToOne(fetch = FetchType.LAZY)
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    User author;

    @Column(name = "creation_date")
    LocalDateTime creationDate;
}
