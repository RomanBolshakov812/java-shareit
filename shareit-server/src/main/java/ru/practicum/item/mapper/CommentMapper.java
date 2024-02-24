package ru.practicum.item.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, Item item, User author,
                                    LocalDateTime created) {
        return new Comment(
                null,
                commentDto.getText(),
                item,
                author,
                created
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                ItemMapper.toItemDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentDto> toListCommentDto(List<Comment> comments) {
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(toCommentDto(comment));
        }
        return commentsDto;
    }
}
