package ru.practicum.shareit.item.model;

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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Вещь.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"owner", "request"})
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items")
public class Item extends AbstractEntity {
    String name;

    String description;

    @Column(name = "is_available")
    Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    ItemRequest request;
}
