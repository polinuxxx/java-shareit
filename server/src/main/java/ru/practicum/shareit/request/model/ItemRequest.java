package ru.practicum.shareit.request.model;

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
 * Запрос.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "requestor")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests")
public class ItemRequest extends AbstractEntity {
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    User requestor;

    @Column(name = "creation_date")
    LocalDateTime creationDate;
}
