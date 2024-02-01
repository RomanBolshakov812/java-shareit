package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNullException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    private RequestService requestService;
    private Integer requestId;
    private Request request;
    private RequestDtoIn requestDtoIn;
    private Integer userId;
    private User user;
    private Item item;
    private List<Item> items;
    private LocalDateTime created;
    private Integer wrongUserId;
    private Integer wrongRequestId;

    @BeforeEach
    void beforeEach() {
        wrongUserId = 100;
        wrongRequestId = 100;
        created = LocalDateTime.now();
        requestId = 1;
        requestService = new RequestServiceImpl(requestRepository, userRepository);
        userId = 1;
        user = new User(1, "Name", "mail@mail.ru");
        item = new Item(1, "item1", "item1 description",
                true, 1, null);
        items = new ArrayList<>();
        items.add(item);
        request = new Request(requestId, "description", user, created, null);
        requestDtoIn = new RequestDtoIn(requestId, "description", created);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        lenient().when(userRepository.findById(wrongUserId)).thenReturn(Optional.empty());
        when(requestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(request));
        lenient().when(requestRepository.findById(wrongRequestId)).thenReturn(Optional.empty());
    }

    @Test
    void addRequest_whenUserNotFound_thenReturnedEntityNullException() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> requestService.addRequest(requestDtoIn, wrongUserId));

        assertEquals("Пользователь с id = 100 не найден!", exception.getMessage());
    }

    @Test
    void addRequest_whenUserIsFound_thenReturnedInvoked() {
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        RequestDtoOut expectedRequest = RequestMapper.toRequestDtoOut(request);

        RequestDtoOut actualRequest = requestService.addRequest(requestDtoIn, userId);
        actualRequest.setCreated(created);

        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    void getRequest_whenUserNotFound_thenReturnedEntityNullException() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> requestService.getRequest(requestId, wrongUserId));

        assertEquals("Пользователь с id = 100 не найден!", exception.getMessage());
    }

    @Test
    void getRequest_whenRequestNotFound_thenReturnedEntityNullException() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> requestService.getRequest(wrongRequestId, userId));

        assertEquals("Запрос с id = 100 не найден!", exception.getMessage());
    }

    @Test
    void getRequest_whenAllOk_thenReturnedInvoked() {
        item.setRequest(request);
        RequestDtoOut expectedRequestDtoWithItems = RequestMapper
                .toRequestDtoOut(request);

        RequestDtoOut actualRequestDto = requestService.getRequest(requestId, userId);

        assertEquals(expectedRequestDtoWithItems, actualRequestDto);
    }

    @Test
    void getRequestsByRequestorId_whenUserNotFound_thenReturnedEntityNullException() {
        EntityNullException exception = assertThrows(EntityNullException.class,
                () -> requestService.getRequestsByRequestorId(wrongUserId, 0, 1));

        assertEquals("Передан неверный id пользователя: id = 100!", exception.getMessage());
    }

    @Test
    void getRequestsByRequestorId_whenAllOk_thenReturnedInvoked() {
        List<Request> requestsList = new ArrayList<>();
        requestsList.add(request);
        Page<Request> requestsPage = new PageImpl<>(requestsList);
        when(requestRepository.findRequestsByRequestorIdOrderByCreatedDesc(anyInt(),
                any(Pageable.class))).thenReturn(requestsPage);
        List<RequestDtoOut> expectedRequestList = RequestMapper.toListRequestDtoOut(requestsList);

        List<RequestDtoOut> actualRequestsList = requestService
                .getRequestsByRequestorId(userId, 0, 1);

        assertEquals(expectedRequestList, actualRequestsList);
    }

    @Test
    void getRequestsOtherUsers() {
        List<Request> requestsList = new ArrayList<>();
        requestsList.add(request);
        Page<Request> requestsPage = new PageImpl<>(requestsList);
        when(requestRepository.findRequestsByOtherUsers(anyInt(), any(Pageable.class)))
                .thenReturn(requestsPage);
        List<RequestDtoOut> expectedRequestList = RequestMapper.toListRequestDtoOut(requestsList);

        List<RequestDtoOut> actualRequestsList = requestService
                .getRequestsOtherUsers(userId, 0, 1);

        assertEquals(expectedRequestList, actualRequestsList);
    }
}
