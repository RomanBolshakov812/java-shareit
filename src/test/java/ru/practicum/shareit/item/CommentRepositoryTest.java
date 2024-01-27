package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
@SpringJUnitConfig
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Integer itemId;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private LocalDateTime created;

    @BeforeEach
    public  void  beforeEach() {
        created = LocalDateTime.now();
        user1 = new User(null, "User1", "mail1@mail.ru");
        user2 = new User(null, "User2", "mail2@mail.ru");
        item1 = new Item(null, "item1", "d", true,
                1, null);
        item2 = new Item(null, "item2", "d", true,
                2, null);
        comment1 = new Comment(null, "Comment 1", item1, user2, created);
        comment2 = new Comment(null, "Comment 2", item2, user1, created);
        comment3 = new Comment(null, "Comment 3", item1, user2, created);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
    }

    @Test
    void findAllCommentByItemId() {
        List<Comment> expectedComments = new ArrayList<>();
        expectedComments.add(comment1);
        expectedComments.add(comment3);

        List<Comment> actualComments = commentRepository.findAllCommentByItemId(1);

        assertEquals(expectedComments, actualComments);
    }

    @AfterEach
    public void deleteAll() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}
