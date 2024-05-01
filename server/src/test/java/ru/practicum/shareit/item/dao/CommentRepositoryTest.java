package ru.practicum.shareit.item.dao;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Тесты для {@link CommentRepository}
 */
@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {
    private final CommentRepository commentRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private User user;

    private Item firstItem;

    private Item secondItem;

    private Comment firstComment;

    private Comment secondComment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("WilliamTurner")
                .email("bootstrap@gmail.com")
                .build();
        firstItem = Item.builder()
                .name("item 1")
                .description("description")
                .available(true)
                .build();
        secondItem = Item.builder()
                .name("item 2")
                .description("something")
                .available(true)
                .build();
        firstComment = Comment.builder()
                .text("comment 1")
                .item(firstItem)
                .author(user)
                .build();
        secondComment = Comment.builder()
                .text("comment 2")
                .item(secondItem)
                .author(user)
                .build();
    }

    @Test
    void findByItemId() {
        userRepository.save(user);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);

        commentRepository.save(firstComment);
        commentRepository.save(secondComment);

        List<Comment> comments = commentRepository.findByItemId(firstItem.getId());

        assertThat(comments, hasSize(1));
        assertThat(comments.get(0).getText(), equalTo(firstComment.getText()));
        assertThat(comments.get(0).getItem().getId(), equalTo(firstItem.getId()));
        assertThat(comments.get(0).getAuthor().getId(), equalTo(user.getId()));
    }

    @Test
    void findByItemIdIn() {
        userRepository.save(user);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);

        commentRepository.save(firstComment);
        commentRepository.save(secondComment);

        List<Comment> comments = commentRepository.findByItemIdIn(List.of(firstItem.getId(), secondItem.getId()));

        assertThat(comments, hasSize(2));
        assertThat(comments.get(0).getText(), equalTo(firstComment.getText()));
        assertThat(comments.get(0).getItem().getId(), equalTo(firstItem.getId()));
        assertThat(comments.get(0).getAuthor().getId(), equalTo(user.getId()));
        assertThat(comments.get(1).getText(), equalTo(secondComment.getText()));
        assertThat(comments.get(1).getItem().getId(), equalTo(secondItem.getId()));
        assertThat(comments.get(1).getAuthor().getId(), equalTo(user.getId()));
    }
}