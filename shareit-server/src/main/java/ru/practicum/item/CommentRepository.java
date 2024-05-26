package ru.practicum.item;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllCommentByItemId(Integer itemId);
}
