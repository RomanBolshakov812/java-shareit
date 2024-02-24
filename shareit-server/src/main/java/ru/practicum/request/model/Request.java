package ru.practicum.request.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

@Data
@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @OneToMany
    @JoinColumn(name = "request_id")
    private List<Item> items;
}
