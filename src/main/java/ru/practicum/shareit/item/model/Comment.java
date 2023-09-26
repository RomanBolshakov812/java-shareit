package ru.practicum.shareit.item.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne
    private Item item;

    @ManyToOne
    private User author;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}

