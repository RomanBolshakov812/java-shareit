package ru.practicum.item.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.user.model.User;

@Data
@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
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

