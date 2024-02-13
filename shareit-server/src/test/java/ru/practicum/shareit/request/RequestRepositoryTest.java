package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
@SpringJUnitConfig
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private Request request1;
    private Request request2;
    private Request request3;

    @BeforeEach
    public void beforeEach() {
        User user1 = new User(null, "Name1", "mail1@mail.ru");
        User user2 = new User(null, "Name2", "mail2@mail.ru");
        LocalDateTime created = LocalDateTime.now();
        request1 = new Request(null, "Request 1", user1,
                created, null);
        request2 = new Request(null, "Request 2", user2,
                created.plusHours(1), null);
        request3 = (new Request(null, "Request 3", user1,
                created.plusHours(2), null));
        userRepository.save(user1);
        userRepository.save(user2);
        requestRepository.save(request1);
        requestRepository.save(request2);
        requestRepository.save(request3);
    }

    @Test
    void findRequestsByRequestorIdOrderByCreatedDesc() {
        List<Request> requestsList = new ArrayList<>();
        requestsList.add(request3);
        requestsList.add(request1);
        Page<Request> expectedRequestPage = new PageImpl<>(requestsList);
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(0, 2, sort);

        Page<Request> actualRequestPage = requestRepository
                .findRequestsByRequestorIdOrderByCreatedDesc(1, page);

        assertEquals(expectedRequestPage.toList(), actualRequestPage.toList());
    }

    @Test
    void findRequestsByOtherUsers() {
        List<Request> requestsList = new ArrayList<>();
        requestsList.add(request3);
        requestsList.add(request1);
        Page<Request> expectedRequestPage = new PageImpl<>(requestsList);
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(0, 2, sort);

        Page<Request> actualRequestPage = requestRepository
                .findRequestsByOtherUsers(2, page);

        assertEquals(expectedRequestPage.toList(), actualRequestPage.toList());
    }

    @AfterEach
    public void deleteAll() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }
}
