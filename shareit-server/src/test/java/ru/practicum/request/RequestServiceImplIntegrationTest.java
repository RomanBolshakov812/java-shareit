package ru.practicum.request;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.request.dto.RequestDtoIn;
import ru.practicum.request.dto.RequestDtoOut;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserRepository;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private RequestDtoIn requestDtoIn1;
    private RequestDtoIn requestDtoIn2;
    private RequestDtoOut requestDtoOut1;
    private RequestDtoOut requestDtoOut2;
    private UserDto userDto;
    private Integer from;
    private Integer size;
    private LocalDateTime created;

    @BeforeEach
    public void beforeEach() {
        created = LocalDateTime.now();
        userDto = new UserDto(null, "User1", "mail1@mail.ru");
        userDto = userService.addUser(userDto);
        requestDtoIn1 = new RequestDtoIn(null, "d1", created);
        requestDtoIn2 = new RequestDtoIn(null, "d2", created.plusHours(1));
        requestDtoOut1 = requestService.addRequest(requestDtoIn1, userDto.getId());
        requestDtoOut2 = requestService.addRequest(requestDtoIn2, userDto.getId());
    }

    @Test
    void getRequestsByRequestorId() {
        TypedQuery<Request> query = em.createQuery("Select r from Request r "
                + "where r.requestor.id = :id order by r.created desc", Request.class);
        query.setParameter("id", userDto.getId());
        List<Request> queryResultList = query.getResultList();
        from = 0;
        size = 2;
        List<RequestDtoOut> expectedRequestsList = RequestMapper
                .toListRequestDtoOut(queryResultList);

        List<RequestDtoOut> actualRequestsList =
                requestService.getRequestsByRequestorId(userDto.getId(), from, size);

        assertThat(actualRequestsList.size(), equalTo(2));
        assertThat(expectedRequestsList, equalTo(actualRequestsList));
        assertThat(actualRequestsList.get(0), equalTo(requestDtoOut2));
        assertThat(actualRequestsList.get(1), equalTo(requestDtoOut1));
    }

    @AfterEach
    public void deleteAll() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }
}
